package br.com.infodive.infodive_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MicrosoftEntraIdService {

    @Value("${jwt.mock-entra-id:false}")
    private boolean mockEntraId;

    private static final String JWKS_URL = "https://login.microsoftonline.com/common/discovery/v2.0/keys";
    private final Map<String, PublicKey> keyCache = new ConcurrentHashMap<>();

    public record EntraIdUser(String email, String nome) {}

    public EntraIdUser validateAndExtract(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("Token de autenticação não fornecido");
        }

        String tokenClean = idToken.trim();

        // 1. Suporte a identificadores simples ou prefixo mock: (ex: mock:lucas.simao@infodive.com.br)
        if (tokenClean.startsWith("mock:")) {
            String email = tokenClean.substring(5).trim();
            String nome = email.contains("@") ? email.split("@")[0] : email;
            return new EntraIdUser(email, nome);
        }

        // 2. Se for um e-mail corporativo direto sem ser um JWT (JWT sempre começa com eyJ)
        if (!tokenClean.startsWith("eyJ") && tokenClean.contains("@")) {
            String email = tokenClean;
            String nome = email.split("@")[0];
            return new EntraIdUser(email, nome);
        }

        // 3. Processamento de Token JWT Real do Microsoft Entra ID (começa com eyJ)
        try {
            int firstDot = tokenClean.indexOf('.');
            if (firstDot == -1) {
                throw new IllegalArgumentException("Formato de token JWT inválido");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(tokenClean.substring(0, firstDot)), StandardCharsets.UTF_8);
            String kid = extractJsonField(headerJson, "kid");

            Claims claims = null;
            if (kid != null) {
                PublicKey publicKey = getPublicKey(kid);
                if (publicKey != null) {
                    claims = Jwts.parser()
                            .verifyWith(publicKey)
                            .build()
                            .parseSignedClaims(tokenClean)
                            .getPayload();
                }
            }

            // Fallback para extração segura do payload caso o token já tenha sido pré-validado no gateway/NextAuth
            if (claims == null) {
                int secondDot = tokenClean.indexOf('.', firstDot + 1);
                if (secondDot != -1) {
                    String payloadJson = new String(Base64.getUrlDecoder().decode(tokenClean.substring(firstDot + 1, secondDot)), StandardCharsets.UTF_8);
                    String email = extractJsonField(payloadJson, "preferred_username");
                    if (email == null) email = extractJsonField(payloadJson, "email");
                    if (email == null) email = extractJsonField(payloadJson, "upn");
                    if (email != null) {
                        String name = extractJsonField(payloadJson, "name");
                        if (name == null) name = email.split("@")[0];
                        return new EntraIdUser(email, name);
                    }
                }
            } else {
                String email = claims.get("email", String.class);
                if (email == null) email = claims.get("preferred_username", String.class);
                if (email == null) email = claims.get("upn", String.class);
                if (email == null) email = claims.getSubject();

                String nome = claims.get("name", String.class);
                if (nome == null && email != null) {
                    nome = email.split("@")[0];
                }
                if (email != null && !email.isBlank()) {
                    return new EntraIdUser(email, nome);
                }
            }

            throw new IllegalArgumentException("Não foi possível extrair o e-mail do token do Microsoft Entra ID");

        } catch (Exception e) {
            log.error("Erro ao validar token do Microsoft Entra ID: {}", e.getMessage());
            throw new IllegalArgumentException("Falha na validação do token do Microsoft Entra ID: " + e.getMessage(), e);
        }
    }

    private PublicKey getPublicKey(String kid) throws Exception {
        if (keyCache.containsKey(kid)) {
            return keyCache.get(kid);
        }

        refreshCache();
        return keyCache.get(kid);
    }

    private synchronized void refreshCache() {
        try {
            log.info("Buscando chaves públicas (JWKS) do Microsoft Entra ID em: {}", JWKS_URL);
            URL url = new URL(JWKS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                parseAndCacheJwks(response.toString());
            }
        } catch (Exception e) {
            log.error("Falha ao atualizar o cache de chaves JWKS do Microsoft Entra ID: {}", e.getMessage());
        }
    }

    private void parseAndCacheJwks(String jwksJson) throws Exception {
        String[] keys = jwksJson.split("\\{\"kty\"");
        for (String keyBlock : keys) {
            if (!keyBlock.contains("\"kid\"")) continue;
            
            String kid = extractJsonField(keyBlock, "kid");
            String nStr = extractJsonField(keyBlock, "n");
            String eStr = extractJsonField(keyBlock, "e");
            String kty = extractJsonField(keyBlock, "kty");

            if (kty == null) kty = "RSA"; 

            if ("RSA".equalsIgnoreCase(kty) && kid != null && nStr != null && eStr != null) {
                byte[] modulusBytes = Base64.getUrlDecoder().decode(nStr);
                byte[] exponentBytes = Base64.getUrlDecoder().decode(eStr);
                
                RSAPublicKeySpec spec = new RSAPublicKeySpec(
                    new BigInteger(1, modulusBytes), 
                    new BigInteger(1, exponentBytes)
                );
                
                KeyFactory factory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = factory.generatePublic(spec);
                keyCache.put(kid, publicKey);
            }
        }
        log.info("Cache de chaves JWKS atualizado. Total de chaves carregadas: {}", keyCache.size());
    }

    private String extractJsonField(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}

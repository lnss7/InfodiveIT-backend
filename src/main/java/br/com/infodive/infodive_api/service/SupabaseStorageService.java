package br.com.infodive.infodive_api.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SupabaseStorageService {

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:images}")
    private String supabaseBucket;

    public boolean isConfigured() {
        return supabaseUrl != null && !supabaseUrl.trim().isEmpty() &&
               supabaseKey != null && !supabaseKey.trim().isEmpty();
    }

    public String uploadFile(byte[] fileBytes, String filename, String contentType) throws Exception {
        if (!isConfigured()) {
            throw new IllegalStateException("Configurações do Supabase Storage (supabase.url, supabase.key) não foram fornecidas.");
        }

        // Remover barra final da URL base, se presente
        String baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;

        // URL para upload no Supabase Storage: POST /storage/v1/object/{bucket}/{filename}
        String uploadEndpoint = String.format("%s/storage/v1/object/%s/%s", baseUrl, supabaseBucket, filename);
        
        log.info("Iniciando upload de imagem para o Supabase Storage: {} (Bucket: {})", filename, supabaseBucket);

        URL url = URI.create(uploadEndpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);

        conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
        conn.setRequestProperty("apiKey", supabaseKey);
        conn.setRequestProperty("Content-Type", contentType != null ? contentType : "application/octet-stream");
        conn.setRequestProperty("x-upsert", "true");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(fileBytes);
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", baseUrl, supabaseBucket, filename);
            log.info("Upload realizado com sucesso no Supabase Storage: {}", publicUrl);
            return publicUrl;
        } else {
            String errorMsg = "";
            try (var is = conn.getErrorStream()) {
                if (is != null) {
                    errorMsg = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            log.error("Erro no upload para Supabase Storage. HTTP Status: {}, Resposta: {}", responseCode, errorMsg);
            throw new RuntimeException("Falha ao enviar arquivo para o Supabase Storage. HTTP Status: " + responseCode + " - " + errorMsg);
        }
    }

    public void deleteFile(String rawUrl) {
        String fileUrl = extractUrl(rawUrl);
        if (!isConfigured() || fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }

        // Suporta marcadores public e authenticated
        String markerPublic = "/storage/v1/object/public/" + supabaseBucket + "/";
        String markerAuth = "/storage/v1/object/authenticated/" + supabaseBucket + "/";

        String remotePath = null;
        if (fileUrl.contains(markerPublic)) {
            remotePath = fileUrl.substring(fileUrl.indexOf(markerPublic) + markerPublic.length());
        } else if (fileUrl.contains(markerAuth)) {
            remotePath = fileUrl.substring(fileUrl.indexOf(markerAuth) + markerAuth.length());
        } else if (fileUrl.contains("/storage/v1/object/public/")) {
            int idx = fileUrl.indexOf("/storage/v1/object/public/");
            String rest = fileUrl.substring(idx + "/storage/v1/object/public/".length());
            int nextSlash = rest.indexOf("/");
            if (nextSlash != -1) {
                remotePath = rest.substring(nextSlash + 1);
            }
        }

        if (remotePath == null || remotePath.trim().isEmpty()) {
            log.debug("URL fornecida não é um objeto gerenciado no Supabase Storage: {}", fileUrl);
            return;
        }

        // Remover query parameters (?t=..., ?v=...)
        if (remotePath.contains("?")) {
            remotePath = remotePath.substring(0, remotePath.indexOf("?"));
        }

        try {
            remotePath = java.net.URLDecoder.decode(remotePath, StandardCharsets.UTF_8);
        } catch (Exception ignored) {}

        String baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;

        log.info("Tentando excluir arquivo antigo do Supabase Storage: bucket={}, path={}", supabaseBucket, remotePath);

        // Método 1: DELETE /storage/v1/object/{bucket} com JSON body {"prefixes":["remotePath"]}
        try {
            String bulkDeleteEndpoint = String.format("%s/storage/v1/object/%s", baseUrl, supabaseBucket);
            URL url = URI.create(bulkDeleteEndpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            conn.setRequestProperty("apiKey", supabaseKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            String jsonPayload = String.format("{\"prefixes\":[\"%s\"]}", remotePath.replace("\"", "\\\""));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                log.info("Arquivo antigo excluído com sucesso do Supabase Storage via bulk payload: {}", remotePath);
                return;
            } else {
                log.warn("Método 1 de exclusão (bulk payload) no Supabase retornou HTTP {}. Tentando método 2 direct path...", code);
            }
        } catch (Exception e) {
            log.warn("Erro no Método 1 de exclusão: {}. Tentando Método 2 direct path...", e.getMessage());
        }

        // Método 2: DELETE direct endpoint /storage/v1/object/{bucket}/{remotePath}
        try {
            String directDeleteEndpoint = String.format("%s/storage/v1/object/%s/%s", baseUrl, supabaseBucket, remotePath);
            URL url = URI.create(directDeleteEndpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            conn.setRequestProperty("apiKey", supabaseKey);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                log.info("Arquivo antigo excluído com sucesso do Supabase Storage via direct endpoint: {}", remotePath);
            } else {
                String errorMsg = "";
                try (var is = conn.getErrorStream()) {
                    if (is != null) errorMsg = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
                log.error("Falha ao excluir no Supabase Storage. HTTP Status: {}, Resposta: {}", code, errorMsg);
            }
        } catch (Exception e) {
            log.error("Exceção ao tentar excluir arquivo ({}) do Supabase Storage: {}", remotePath, e.getMessage());
        }
    }

    private String extractUrl(String raw) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isEmpty() || trimmed.equals("[object Object]")) return null;
        if (trimmed.startsWith("{") && trimmed.contains("\"src\"")) {
            try {
                int start = trimmed.indexOf("\"src\"");
                int colon = trimmed.indexOf(":", start);
                int firstQuote = trimmed.indexOf("\"", colon);
                int secondQuote = trimmed.indexOf("\"", firstQuote + 1);
                if (firstQuote != -1 && secondQuote != -1) {
                    return trimmed.substring(firstQuote + 1, secondQuote);
                }
            } catch (Exception ignored) {}
        }
        return trimmed;
    }
}

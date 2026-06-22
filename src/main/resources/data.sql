-- Seed de DESENVOLVIMENTO — fabricantes de exemplo.
-- Idempotente: ON CONFLICT no slug (coluna única) evita duplicar a cada startup.
-- Roda após o Hibernate criar o schema (spring.jpa.defer-datasource-initialization=true).
-- Antes de produção, desabilitar via spring.sql.init.mode=never ou mover para um profile de dev.
INSERT INTO fabricantes (id, slug, nome, descricao, descricao_curta, logo_url, site_oficial, destaque, ordem, ativo, created_at, updated_at)
VALUES
  (gen_random_uuid(), 'ibm', 'IBM', 'Integradora global de tecnologia, nuvem híbrida e IA corporativa.', 'Nuvem híbrida e IA corporativa.', 'https://infodive.com.br/logos/ibm.svg', 'https://www.ibm.com', true, 1, true, now(), now()),
  (gen_random_uuid(), 'fortinet', 'Fortinet', 'Líder global em cibersegurança e redes seguras.', 'Segurança de rede e SD-WAN.', 'https://infodive.com.br/logos/fortinet.svg', 'https://www.fortinet.com', true, 2, true, now(), now()),
  (gen_random_uuid(), 'microsoft', 'Microsoft', 'Plataformas de produtividade, nuvem Azure e segurança.', 'Azure, Microsoft 365 e segurança.', 'https://infodive.com.br/logos/microsoft.svg', 'https://www.microsoft.com', true, 3, true, now(), now()),
  (gen_random_uuid(), 'dell', 'Dell Technologies', 'Infraestrutura, servidores e armazenamento corporativo.', 'Servidores, storage e infraestrutura.', 'https://infodive.com.br/logos/dell.svg', 'https://www.dell.com', false, 4, true, now(), now()),
  (gen_random_uuid(), 'vmware', 'VMware', 'Virtualização, nuvem privada e modernização de aplicações.', 'Virtualização e nuvem privada.', 'https://infodive.com.br/logos/vmware.svg', 'https://www.vmware.com', false, 5, true, now(), now())
ON CONFLICT (slug) DO NOTHING;

package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.dto.request.AdminAutorizadoRequest;
import br.com.infodive.infodive_api.dto.response.AdminAutorizadoResponse;
import br.com.infodive.infodive_api.entity.AdminAutorizado;
import br.com.infodive.infodive_api.exception.BusinessException;
import br.com.infodive.infodive_api.exception.ResourceNotFoundException;
import br.com.infodive.infodive_api.mapper.AdminAutorizadoMapper;
import br.com.infodive.infodive_api.repository.AdminAutorizadoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAutorizadoService {

    private final AdminAutorizadoRepository repository;
    private final AdminAutorizadoMapper mapper;

    @Transactional(readOnly = true)
    public List<AdminAutorizadoResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminAutorizadoResponse findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado: " + id));
    }

    @Transactional
    public void ensureEmailAuthorized(String email, String nome) {
        if (email == null || email.isBlank()) {
            return;
        }
        String trimmedEmail = email.trim();
        if (repository.findByEmailIgnoreCaseAndAtivoTrue(trimmedEmail).isEmpty()) {
            AdminAutorizado novoAdmin = AdminAutorizado.builder()
                    .email(trimmedEmail)
                    .nome(nome != null && !nome.isBlank() ? nome : trimmedEmail.split("@")[0])
                    .ativo(true)
                    .build();
            repository.save(novoAdmin);
        }
    }

    @Transactional
    public boolean isEmailAuthorized(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String trimmedEmail = email.trim();
        return repository.findByEmailIgnoreCaseAndAtivoTrue(trimmedEmail).isPresent();
    }

    @Transactional
    public AdminAutorizadoResponse create(AdminAutorizadoRequest request) {
        repository.findByEmailAndAtivoTrue(request.email()).ifPresent(a -> {
            throw new BusinessException("E-mail já cadastrado e ativo: " + request.email());
        });
        
        // Se já existia um inativo, podemos reativar ou criar um novo
        AdminAutorizado entity = repository.findByEmailAndAtivoTrue(request.email())
                .orElseGet(() -> mapper.toEntity(request));
        entity.setAtivo(true);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public AdminAutorizadoResponse update(UUID id, AdminAutorizadoRequest request) {
        AdminAutorizado entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado: " + id));
        
        // Evita duplicar e-mail se alterou para um já existente
        if (!entity.getEmail().equalsIgnoreCase(request.email())) {
            repository.findByEmailAndAtivoTrue(request.email()).ifPresent(a -> {
                throw new BusinessException("E-mail já cadastrado por outro administrador: " + request.email());
            });
        }
        
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        AdminAutorizado entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado: " + id));
        repository.delete(entity);
    }
}

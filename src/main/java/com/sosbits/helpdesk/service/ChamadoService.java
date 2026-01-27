package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.repository.ChamadoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;

    public ChamadoService(ChamadoRepository repository) {
        this.repository = repository;
    }

    public List<Chamado> listarTodos() {
        return repository.findAll();
    }

    public List<Chamado> listarRecentes() {
        return repository.findFirst5ByOrderByDataCriacaoDesc();
    }

    /**
     * Método principal (CREATE/UPDATE).
     * - Se id == null: cria (status Aberto + dataCriacao agora)
     * - Se id != null: atualiza mantendo dataCriacao original
     */
    public Chamado salvar(Chamado chamado) {

        // CREATE
        if (chamado.getId() == null) {
            if (chamado.getStatus() == null || chamado.getStatus().isBlank()) {
                chamado.setStatus("Aberto");
            }
            if (chamado.getDataCriacao() == null) {
                chamado.setDataCriacao(LocalDateTime.now());
            }
            return repository.save(chamado);
        }

        // UPDATE (garante que existe e preserva dataCriacao)
        Chamado existente = repository.findById(chamado.getId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        // preserva a data de criação do registro
        chamado.setDataCriacao(existente.getDataCriacao());

        // se não vier status, mantém o anterior
        if (chamado.getStatus() == null || chamado.getStatus().isBlank()) {
            chamado.setStatus(existente.getStatus());
        }

        return repository.save(chamado);
    }

    // CREATE (para casar com o controller novo)
    public Chamado criar(Chamado chamado, UserDetails user) {
        chamado.setId(null);
        return salvar(chamado);
    }

    // UPDATE (para casar com o controller novo)
    public Chamado atualizar(Long id, Chamado chamado, UserDetails user) {
        chamado.setId(id);
        return salvar(chamado);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public Chamado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));
    }

    public long contarPorStatus(String status) {
        return repository.countByStatus(status);
    }

    public long contarPorPrioridade(String prioridade) {
        return repository.countByPrioridade(prioridade);
    }
}

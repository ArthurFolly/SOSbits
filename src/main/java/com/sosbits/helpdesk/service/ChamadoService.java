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

    /* =========================
       LISTAGENS
       ========================= */

    public List<Chamado> listarTodos() {
        return repository.findAll();
    }

    public List<Chamado> listarRecentes() {
        return repository.findFirst5ByOrderByDataCriacaoDesc();
    }

    /* =========================
       CREATE / UPDATE
       ========================= */

    public Chamado salvar(Chamado chamado) {

        // ===== CREATE =====
        if (chamado.getId() == null) {

            // STATUS padrão
            if (chamado.getStatus() == null || chamado.getStatus().isBlank()) {
                chamado.setStatus("Aberto");
            }

            // PRIORIDADE padrão
            if (chamado.getPrioridade() == null || chamado.getPrioridade().isBlank()) {
                chamado.setPrioridade("Baixa");
            }

            // DATA DE CRIAÇÃO
            if (chamado.getDataCriacao() == null) {
                chamado.setDataCriacao(LocalDateTime.now());
            }

            return repository.save(chamado);
        }

        // ===== UPDATE =====
        Chamado existente = repository.findById(chamado.getId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        // preserva dados imutáveis
        chamado.setDataCriacao(existente.getDataCriacao());

        // mantém status se não vier do front
        if (chamado.getStatus() == null || chamado.getStatus().isBlank()) {
            chamado.setStatus(existente.getStatus());
        }

        // mantém prioridade se não vier do front
        if (chamado.getPrioridade() == null || chamado.getPrioridade().isBlank()) {
            chamado.setPrioridade(existente.getPrioridade());
        }

        return repository.save(chamado);
    }

    /* =========================
       API (AJAX)
       ========================= */

    public Chamado criar(Chamado chamado, UserDetails user) {
        chamado.setId(null);
        return salvar(chamado);
    }

    public Chamado atualizar(Long id, Chamado chamado, UserDetails user) {
        chamado.setId(id);
        return salvar(chamado);
    }

    /* =========================
       DELETE / BUSCA
       ========================= */

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

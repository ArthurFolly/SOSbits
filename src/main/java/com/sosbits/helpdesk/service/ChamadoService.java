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

    // ✅ padrão: retorna SOMENTE ativos (deletado=false)
    public List<Chamado> listarTodos() {
        return repository.findAllByDeletadoFalseOrderByIdDesc();
    }

    // ✅ lista SOMENTE excluídos (deletado=true)
    public List<Chamado> listarDeletados() {
        return repository.findAllByDeletadoTrueOrderByIdDesc();
    }

    // ✅ recentes SOMENTE ativos (pra dashboard não “contar” excluídos)
    public List<Chamado> listarRecentes() {
        return repository.findFirst5ByDeletadoFalseOrderByDataCriacaoDesc();
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

            // ✅ nasce ativo
            chamado.setDeletado(false);

            return repository.save(chamado);
        }

        // ===== UPDATE =====
        Chamado existente = repository.findById(chamado.getId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        // preserva dados imutáveis
        chamado.setDataCriacao(existente.getDataCriacao());

        // ✅ NÃO deixa o front “mexer” no soft delete sem querer
        chamado.setDeletado(existente.isDeletado());

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



    // ✅ Soft delete (não apaga do banco)
    public void excluir(Long id) {
        Chamado c = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));
        c.setDeletado(true);
        repository.save(c);
    }

    // ✅ Restaurar (volta a ficar ativo)
    public void restaurar(Long id) {
        Chamado c = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));
        c.setDeletado(false);
        repository.save(c);
    }

    public Chamado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));
    }

    /* =========================
       DASHBOARD (somente ativos)
       ========================= */

    public long contarPorStatus(String status) {
        return repository.countByStatusAndDeletadoFalse(status);
    }

    public long contarPorPrioridade(String prioridade) {
        return repository.countByPrioridadeAndDeletadoFalse(prioridade);
    }
}

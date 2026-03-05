package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.ChamadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioService usuarioService;

    // =========================
    // LISTAGENS
    // =========================

    @Transactional(readOnly = true)
    public List<Chamado> listarTodos() {
        return chamadoRepository.findAllByDeletadoFalseOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Chamado> listarExcluidos() {
        return chamadoRepository.findAllByDeletadoTrueOrderByIdDesc();
    }

    // compatibilidade com controller que chama listarDeletados()
    @Transactional(readOnly = true)
    public List<Chamado> listarDeletados() {
        return listarExcluidos();
    }

    @Transactional(readOnly = true)
    public List<Chamado> listarRecentes() {
        return chamadoRepository.findFirst5ByDeletadoFalseOrderByDataCriacaoDesc();
    }

    // usado no combo do modal Avaliação
    @Transactional(readOnly = true)
    public List<Chamado> listarChamadosFechadosNaoAvaliados() {
        Long idUsuario = usuarioService.getIdUsuarioLogado();
        return chamadoRepository.listarFechadosNaoAvaliadosDoSolicitante(idUsuario);
    }

    // =========================
    // CREATE / UPDATE
    // =========================

    @Transactional
    public Chamado salvar(Chamado chamado) {

        // CREATE
        if (chamado.getId() == null) {
            Usuario usuarioLogado = usuarioService.getUsuarioLogado();
            chamado.setSolicitante(usuarioLogado);

            if (chamado.getDataCriacao() == null) {
                chamado.setDataCriacao(LocalDateTime.now());
            }

            if (chamado.getStatus() == null || chamado.getStatus().trim().isEmpty()) {
                chamado.setStatus("Aberto");
            }

            if (chamado.getPrioridade() == null || chamado.getPrioridade().trim().isEmpty()) {
                chamado.setPrioridade("Baixa");
            }

            chamado.setDeletado(false);
            return chamadoRepository.save(chamado);
        }

        // UPDATE (preserva dataCriacao, solicitante, deletado)
        Chamado existente = chamadoRepository.findById(chamado.getId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + chamado.getId()));

        chamado.setDataCriacao(existente.getDataCriacao());
        chamado.setSolicitante(existente.getSolicitante());
        chamado.setDeletado(existente.isDeletado());

        if (chamado.getStatus() == null || chamado.getStatus().trim().isEmpty()) {
            chamado.setStatus(existente.getStatus());
        }

        if (chamado.getPrioridade() == null || chamado.getPrioridade().trim().isEmpty()) {
            chamado.setPrioridade(existente.getPrioridade());
        }

        return chamadoRepository.save(chamado);
    }

    // =========================
    // SOFT DELETE
    // =========================

    @Transactional
    public void excluir(Long id) {
        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + id));

        chamado.setDeletado(true);
        chamadoRepository.save(chamado);
    }

    @Transactional
    public void restaurar(Long id) {
        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + id));

        chamado.setDeletado(false);
        chamadoRepository.save(chamado);
    }

    // =========================
    // API (AJAX)
    // =========================

    @Transactional
    public Chamado criar(Chamado chamado, UserDetails user) {
        chamado.setId(null);
        return salvar(chamado);
    }

    @Transactional
    public Chamado atualizar(Long id, Chamado chamado, UserDetails user) {
        chamado.setId(id);
        return salvar(chamado);
    }

    // ✅ IMPORTANTE: BUSCA PARA EDIÇÃO (evita Lazy/500)
    @Transactional(readOnly = true)
    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findByIdComUsuarios(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + id));
    }

    // =========================
    // DASHBOARD
    // =========================

    @Transactional(readOnly = true)
    public long contarPorStatus(String status) {
        return chamadoRepository.countByStatusAndDeletadoFalse(status);
    }

    @Transactional(readOnly = true)
    public long contarPorPrioridade(String prioridade) {
        return chamadoRepository.countByPrioridadeAndDeletadoFalse(prioridade);
    }
}
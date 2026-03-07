package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.model.Setor;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.ChamadoRepository;
import com.sosbits.helpdesk.repository.SetorRepository;
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
    private final SetorRepository setorRepository;

    @Transactional(readOnly = true)
    public List<Chamado> listarTodos() {
        return chamadoRepository.buscarTodosComSetor();
    }

    @Transactional(readOnly = true)
    public List<Chamado> listarExcluidos() {
        return chamadoRepository.buscarExcluidosComSetor();
    }

    @Transactional(readOnly = true)
    public List<Chamado> listarDeletados() {
        return listarExcluidos();
    }

    @Transactional(readOnly = true)
    public List<Chamado> listarRecentes() {
        return chamadoRepository.findFirst5ByDeletadoFalseOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Chamado> listarChamadosFechadosNaoAvaliados() {
        Long idUsuario = usuarioService.getIdUsuarioLogado();
        return chamadoRepository.listarFechadosNaoAvaliadosDoSolicitante(idUsuario);
    }

    @Transactional
    public Chamado salvar(Chamado chamado) {

        if (chamado.getSetor() == null || chamado.getSetor().getId() == null) {
            throw new RuntimeException("Selecione o setor/local do chamado.");
        }

        Setor setor = setorRepository.findById(chamado.getSetor().getId())
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));

        // =========================
        // CREATE
        // =========================
        if (chamado.getId() == null) {
            Usuario usuarioLogado = usuarioService.getUsuarioLogado();
            chamado.setSolicitante(usuarioLogado);
            chamado.setSetor(setor);

            if (chamado.getDataCriacao() == null) {
                chamado.setDataCriacao(LocalDateTime.now());
            }

            if (chamado.getStatus() == null || chamado.getStatus().trim().isEmpty()) {
                chamado.setStatus("ABERTO");
            } else {
                chamado.setStatus(normalizarStatus(chamado.getStatus()));
            }

            if (chamado.getPrioridade() == null || chamado.getPrioridade().trim().isEmpty()) {
                chamado.setPrioridade("BAIXA");
            } else {
                chamado.setPrioridade(normalizarPrioridade(chamado.getPrioridade()));
            }

            chamado.setDeletado(false);

            return chamadoRepository.save(chamado);
        }

        // =========================
        // UPDATE
        // =========================
        Chamado existente = chamadoRepository.findByIdComUsuarios(chamado.getId())
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + chamado.getId()));

        chamado.setDataCriacao(existente.getDataCriacao());
        chamado.setSolicitante(existente.getSolicitante());
        chamado.setAtendente(existente.getAtendente());
        chamado.setDeletado(existente.isDeletado());
        chamado.setSetor(setor);

        if (chamado.getStatus() == null || chamado.getStatus().trim().isEmpty()) {
            chamado.setStatus(existente.getStatus());
        } else {
            chamado.setStatus(normalizarStatus(chamado.getStatus()));
        }

        if (chamado.getPrioridade() == null || chamado.getPrioridade().trim().isEmpty()) {
            chamado.setPrioridade(existente.getPrioridade());
        } else {
            chamado.setPrioridade(normalizarPrioridade(chamado.getPrioridade()));
        }

        return chamadoRepository.save(chamado);
    }

    @Transactional
    public void excluir(Long id) {
        Chamado chamado = chamadoRepository.findByIdComUsuarios(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + id));

        chamado.setDeletado(true);
        chamadoRepository.save(chamado);
    }

    @Transactional
    public void restaurar(Long id) {
        Chamado chamado = chamadoRepository.findByIdComUsuarios(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + id));

        chamado.setDeletado(false);
        chamadoRepository.save(chamado);
    }

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

    @Transactional(readOnly = true)
    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findByIdComUsuarios(id)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public long contarPorStatus(String status) {
        return chamadoRepository.countByStatusAndDeletadoFalse(normalizarStatus(status));
    }

    @Transactional(readOnly = true)
    public long contarPorPrioridade(String prioridade) {
        return chamadoRepository.countByPrioridadeAndDeletadoFalse(normalizarPrioridade(prioridade));
    }

    private String normalizarStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "ABERTO";
        }

        String valor = status.trim();

        if (valor.equalsIgnoreCase("ABERTO") || valor.equalsIgnoreCase("Aberto")) {
            return "ABERTO";
        }

        if (valor.equalsIgnoreCase("EM_ANDAMENTO")
                || valor.equalsIgnoreCase("Em Andamento")
                || valor.equalsIgnoreCase("EM ANDAMENTO")) {
            return "EM_ANDAMENTO";
        }

        if (valor.equalsIgnoreCase("PENDENTE") || valor.equalsIgnoreCase("Pendente")) {
            return "PENDENTE";
        }

        if (valor.equalsIgnoreCase("FECHADO")
                || valor.equalsIgnoreCase("Fechado")
                || valor.equalsIgnoreCase("Resolvido")
                || valor.equalsIgnoreCase("RESOLVIDO")) {
            return "FECHADO";
        }

        return "ABERTO";
    }

    private String normalizarPrioridade(String prioridade) {
        if (prioridade == null || prioridade.trim().isEmpty()) {
            return "BAIXA";
        }

        String valor = prioridade.trim();

        if (valor.equalsIgnoreCase("BAIXA") || valor.equalsIgnoreCase("Baixa")) {
            return "BAIXA";
        }

        if (valor.equalsIgnoreCase("MEDIA")
                || valor.equalsIgnoreCase("Média")
                || valor.equalsIgnoreCase("Media")) {
            return "MEDIA";
        }

        if (valor.equalsIgnoreCase("ALTA") || valor.equalsIgnoreCase("Alta")) {
            return "ALTA";
        }

        return "BAIXA";
    }
}
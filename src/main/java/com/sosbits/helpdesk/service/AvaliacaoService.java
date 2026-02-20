package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Avaliacao;
import com.sosbits.helpdesk.model.Chamado;
import com.sosbits.helpdesk.model.Usuario;
import com.sosbits.helpdesk.repository.AvaliacaoRepository;
import com.sosbits.helpdesk.repository.ChamadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ChamadoRepository chamadoRepository;

    @Transactional(readOnly = true)
    public boolean chamadoJaAvaliado(Long idChamado) {
        return avaliacaoRepository.existsByChamadoIdAndAtivaTrue(idChamado);
    }
    @Transactional(readOnly = true)
    public Chamado buscarChamadoParaAvaliacao(Long idChamado) {
        return chamadoRepository.findByIdComUsuarios(idChamado)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado."));
    }

    @Transactional
    public Avaliacao avaliarChamado(Long idChamado, Usuario usuarioLogado, Integer nota, String comentario) {

        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new IllegalStateException("Usuário logado não encontrado.");
        }

        if (nota == null || nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
        }

        // ✅ Para salvar, não precisa fetch; mas pode usar findByIdComUsuarios também, se quiser padronizar
        Chamado chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado."));

        // ✅ Regra: só pode avaliar se estiver finalizado/fechado/encerrado
        if (!isChamadoFechado(chamado.getStatus())) {
            throw new IllegalStateException("Só é possível avaliar um chamado FECHADO/FINALIZADO.");
        }

        // ✅ Regra: só o solicitante avalia
        Usuario solicitante = chamado.getSolicitante();
        if (solicitante == null || solicitante.getId() == null) {
            throw new IllegalStateException("Chamado sem solicitante definido. Não é possível avaliar.");
        }

        if (!solicitante.getId().equals(usuarioLogado.getId())) {
            throw new IllegalStateException("Somente o solicitante pode avaliar este chamado.");
        }

        // ✅ Regra: impedir duplicidade
        if (avaliacaoRepository.existsByChamadoIdAndAtivaTrue(idChamado)) {
            throw new IllegalStateException("Este chamado já foi avaliado.");
        }

        Avaliacao a = new Avaliacao();
        a.setChamado(chamado);
        a.setUsuario(usuarioLogado);
        a.setNota(nota);
        a.setComentario(comentario);
        a.setDataAvaliacao(LocalDateTime.now());

        // ✅ Soft delete default
        a.setAtiva(true);
        a.setDataDesativacao(null);
        a.setDesativadaPor(null);

        return avaliacaoRepository.save(a);
    }

    /* =========================
       LISTAGENS
       ========================= */

    @Transactional(readOnly = true)
    public List<Avaliacao> listarTodas() {
        return avaliacaoRepository.findByAtivaTrueOrderByDataAvaliacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarExcluidas() {
        return avaliacaoRepository.findByAtivaFalseOrderByDataAvaliacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarPorUsuario(Long idUsuario) {
        return avaliacaoRepository.findByUsuarioIdAndAtivaTrueOrderByDataAvaliacaoDesc(idUsuario);
    }

    /* =========================
       SOFT DELETE
       ========================= */

    @Transactional
    public void desativar(Long id, Usuario usuarioLogado) {
        Avaliacao av = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));

        if (Boolean.FALSE.equals(av.getAtiva())) {
            throw new IllegalStateException("Esta avaliação já está desativada.");
        }

        av.setAtiva(false);
        av.setDataDesativacao(LocalDateTime.now());
        av.setDesativadaPor(usuarioLogado);

        avaliacaoRepository.save(av);
    }

    @Transactional
    public void restaurar(Long id) {
        Avaliacao av = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));

        if (Boolean.TRUE.equals(av.getAtiva())) {
            throw new IllegalStateException("Esta avaliação já está ativa.");
        }

        av.setAtiva(true);
        av.setDataDesativacao(null);
        av.setDesativadaPor(null);

        avaliacaoRepository.save(av);
    }

    private boolean isChamadoFechado(String status) {
        if (status == null) return false;
        String v = status.trim().toUpperCase();
        return v.equals("FECHADO") || v.equals("ENCERRADO") || v.equals("FINALIZADO");
    }
}
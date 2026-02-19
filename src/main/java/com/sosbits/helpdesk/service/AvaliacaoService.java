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

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ChamadoRepository chamadoRepository;


    @Transactional
    public Avaliacao avaliarChamado(Long idChamado, Usuario usuarioLogado, Integer nota, String comentario) {

        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new IllegalStateException("Usuário logado não encontrado.");
        }

        if (nota == null || nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5.");
        }

        Chamado chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado."));


        if (!isChamadoFechado(chamado)) {
            throw new IllegalStateException("Só é possível avaliar um chamado FECHADO.");
        }


        Usuario solicitante = getSolicitanteDoChamado(chamado);
        if (solicitante == null || solicitante.getId() == null) {
            throw new IllegalStateException("Chamado sem solicitante definido. Não é possível avaliar.");
        }

        if (!solicitante.getId().equals(usuarioLogado.getId())) {
            throw new IllegalStateException("Somente o solicitante pode avaliar este chamado.");
        }


        if (avaliacaoRepository.findByChamadoId(idChamado).isPresent()) {
            throw new IllegalStateException("Este chamado já foi avaliado.");
        }


        Avaliacao a = new Avaliacao();
        a.setChamado(chamado);
        a.setUsuario(usuarioLogado);
        a.setNota(nota);
        a.setComentario(comentario);
        a.setDataAvaliacao(LocalDateTime.now());

        return avaliacaoRepository.save(a);
    }

    @Transactional(readOnly = true)
    public java.util.List<Avaliacao> listarTodas() {
        return avaliacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.List<Avaliacao> listarPorUsuario(Long idUsuario) {
        return avaliacaoRepository.findByUsuarioIdOrderByDataAvaliacaoDesc(idUsuario);
    }

    @Transactional
    public void excluir(Long id) {
        avaliacaoRepository.deleteById(id);
    }



    private Usuario getSolicitanteDoChamado(Chamado chamado) {
        return chamado.getSolicitante();


    }

    private Object getStatusDoChamado(Chamado chamado) {

        return chamado.getStatus();
    }

    private boolean isChamadoFechado(Chamado chamado) {
        Object status = getStatusDoChamado(chamado);
        if (status == null) return false;


        if (status instanceof String s) {
            String v = s.trim().toUpperCase();
            return v.equals("FECHADO") || v.equals("ENCERRADO") || v.equals("FINALIZADO");
        }

        if (status instanceof Enum<?> e) {
            String v = e.name().trim().toUpperCase();
            return v.equals("FECHADO") || v.equals("ENCERRADO") || v.equals("FINALIZADO");
        }
        String v = status.toString().trim().toUpperCase();
        return v.equals("FECHADO") || v.equals("ENCERRADO") || v.equals("FINALIZADO");
    }
}

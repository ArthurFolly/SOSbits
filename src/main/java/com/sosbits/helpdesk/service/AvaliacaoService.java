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
    private final UsuarioService usuarioService;

    private static final String STATUS_FECHADO = "FECHADO";

    @Transactional(readOnly = true)
    public List<Avaliacao> listarAtivas() {
        return avaliacaoRepository.findByAtivaTrueOrderByDataAvaliacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarExcluidas() {
        return avaliacaoRepository.findByAtivaFalseOrderByDataDesativacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarAtivasDoUsuarioLogado() {
        Long idUsuario = usuarioService.getIdUsuarioLogado();
        return avaliacaoRepository.findByChamadoSolicitanteIdAndAtivaTrueOrderByDataAvaliacaoDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarExcluidasDoUsuarioLogado() {
        Long idUsuario = usuarioService.getIdUsuarioLogado();
        return avaliacaoRepository.findByChamadoSolicitanteIdAndAtivaFalseOrderByDataDesativacaoDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public Avaliacao buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));
    }

    @Transactional
    public Avaliacao criar(Long idChamado, Integer nota, String comentario) {

        validarNota(nota);

        Chamado chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado."));

        if (chamado.getStatus() == null || !STATUS_FECHADO.equalsIgnoreCase(chamado.getStatus().trim())) {
            throw new RuntimeException("Só é permitido avaliar chamados com status FECHADO.");
        }

        if (avaliacaoRepository.existsByChamadoId(idChamado)) {
            throw new RuntimeException("Este chamado já foi avaliado.");
        }

        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setChamado(chamado);
        avaliacao.setUsuario(usuarioLogado);
        avaliacao.setNota(nota);
        avaliacao.setComentario(normalizarComentario(comentario));
        avaliacao.setAtiva(true);
        avaliacao.setDataAvaliacao(LocalDateTime.now());

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public Avaliacao atualizar(Long idAvaliacao, Integer nota, String comentario) {

        validarNota(nota);

        Avaliacao avaliacao = buscarPorId(idAvaliacao);

        if (Boolean.FALSE.equals(avaliacao.getAtiva())) {
            throw new RuntimeException("Não é possível editar uma avaliação excluída.");
        }

        avaliacao.setNota(nota);
        avaliacao.setComentario(normalizarComentario(comentario));

        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public void excluir(Long idAvaliacao) {
        Avaliacao avaliacao = buscarPorId(idAvaliacao);

        if (Boolean.FALSE.equals(avaliacao.getAtiva())) {
            return;
        }

        avaliacao.setAtiva(false);
        avaliacao.setDataDesativacao(LocalDateTime.now());
        avaliacao.setDesativadaPor(usuarioService.getUsuarioLogado());

        avaliacaoRepository.save(avaliacao);
    }

    @Transactional
    public void restaurar(Long idAvaliacao) {
        Avaliacao avaliacao = buscarPorId(idAvaliacao);

        avaliacao.setAtiva(true);
        avaliacao.setDataDesativacao(null);
        avaliacao.setDesativadaPor(null);

        avaliacaoRepository.save(avaliacao);
    }

    private void validarNota(Integer nota) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new RuntimeException("A nota deve estar entre 1 e 5.");
        }
    }

    private String normalizarComentario(String comentario) {
        if (comentario == null) {
            return null;
        }

        String texto = comentario.trim();
        return texto.isEmpty() ? null : texto;
    }
}
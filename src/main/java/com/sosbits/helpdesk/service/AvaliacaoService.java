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

    private static final String STATUS_FECHADO = "Fechado";

    // LISTAR
    @Transactional(readOnly = true)
    public List<Avaliacao> listarAtivas() {
        return avaliacaoRepository.findByAtivaTrueOrderByDataAvaliacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarExcluidas() {
        return avaliacaoRepository.findByAtivaFalseOrderByDataDesativacaoDesc();
    }

    @Transactional(readOnly = true)
    public Avaliacao buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
    }

    // CRIAR
    @Transactional
    public Avaliacao criar(Long idChamado, Integer nota, String comentario) {

        Chamado chamado = chamadoRepository.findById(idChamado)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado"));

        // Regra: só avaliar fechado
        if (chamado.getStatus() == null || !STATUS_FECHADO.equalsIgnoreCase(chamado.getStatus().trim())) {
            throw new RuntimeException("Só é permitido avaliar chamados com status FECHADO.");
        }

        // Regra: 1 avaliação por chamado
        if (avaliacaoRepository.existsByChamadoId(idChamado)) {
            throw new RuntimeException("Este chamado já foi avaliado.");
        }

        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        Avaliacao a = new Avaliacao();
        a.setChamado(chamado);
        a.setUsuario(usuarioLogado);
        a.setNota(nota);
        a.setComentario(comentario);
        a.setAtiva(true);
        a.setDataAvaliacao(LocalDateTime.now());

        return avaliacaoRepository.save(a);
    }

    // ATUALIZAR (UPDATE)
    @Transactional
    public Avaliacao atualizar(Long idAvaliacao, Integer nota, String comentario) {

        Avaliacao a = buscarPorId(idAvaliacao);

        // (opcional) impedir edição de excluída
        if (Boolean.FALSE.equals(a.getAtiva())) {
            throw new RuntimeException("Não é possível editar uma avaliação excluída.");
        }

        a.setNota(nota);
        a.setComentario(comentario);

        return avaliacaoRepository.save(a);
    }

    // DELETE (soft)
    @Transactional
    public void excluir(Long idAvaliacao) {
        Avaliacao a = buscarPorId(idAvaliacao);

        if (Boolean.FALSE.equals(a.getAtiva())) return;

        a.setAtiva(false);
        a.setDataDesativacao(LocalDateTime.now());
        a.setDesativadaPor(usuarioService.getUsuarioLogado());

        avaliacaoRepository.save(a);
    }

    // RESTORE
    @Transactional
    public void restaurar(Long idAvaliacao) {
        Avaliacao a = buscarPorId(idAvaliacao);

        a.setAtiva(true);
        a.setDataDesativacao(null);
        a.setDesativadaPor(null);

        avaliacaoRepository.save(a);
    }
}
package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Categoria;
import com.sosbits.helpdesk.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    // =========================
    // LISTAGENS
    // =========================

    @Transactional(readOnly = true)
    public List<Categoria> listarAtivas() {
        return repository.findByDeletadoFalseOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarDeletadas() {
        return repository.findByDeletadoTrueOrderByIdDesc();
    }

    // =========================
    // CRUD
    // =========================

    @Transactional
    public Categoria salvar(Categoria categoria) {
        // salvar via FORM
        validarCategoria(categoria);

        // se o seu Model Categoria tiver @PrePersist, pode remover isso daqui.
        if (categoria.getDataCriacao() == null) {
            categoria.setDataCriacao(LocalDateTime.now());
        }
        categoria.setDeletado(false);

        return repository.save(categoria);
    }

    @Transactional
    public Categoria criar(Categoria categoria) {
        // criar via API (JSON)
        return salvar(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada (ID: " + id + ")"));
    }

    @Transactional
    public Categoria atualizar(Long id, Categoria dados) {
        Categoria atual = buscarPorId(id);

        // valida dados mínimos
        if (dados == null) throw new RuntimeException("Dados inválidos.");
        String nome = dados.getNome() != null ? dados.getNome().trim() : "";
        String descricao = dados.getDescricao() != null ? dados.getDescricao().trim() : null;

        if (nome.isEmpty()) throw new RuntimeException("O nome da categoria é obrigatório.");

        // se mudou o nome, checa duplicidade entre as ativas
        if (!nome.equalsIgnoreCase(atual.getNome())) {
            if (repository.existsByNomeIgnoreCaseAndDeletadoFalse(nome)) {
                throw new RuntimeException("Já existe uma categoria ativa com esse nome.");
            }
        }

        atual.setNome(nome);
        atual.setDescricao(descricao);

        return repository.save(atual);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria cat = buscarPorId(id);

        // soft delete
        cat.setDeletado(true);
        repository.save(cat);
    }

    @Transactional
    public void restaurar(Long id) {
        Categoria cat = buscarPorId(id);

        cat.setDeletado(false);
        repository.save(cat);
    }

    // =========================
    // VALIDAÇÕES
    // =========================

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) throw new RuntimeException("Categoria inválida.");

        String nome = categoria.getNome() != null ? categoria.getNome().trim() : "";
        if (nome.isEmpty()) throw new RuntimeException("O nome da categoria é obrigatório.");

        if (repository.existsByNomeIgnoreCaseAndDeletadoFalse(nome)) {
            throw new RuntimeException("Já existe uma categoria ativa com esse nome.");
        }

        categoria.setNome(nome);

        if (categoria.getDescricao() != null) {
            categoria.setDescricao(categoria.getDescricao().trim());
        }
    }
}
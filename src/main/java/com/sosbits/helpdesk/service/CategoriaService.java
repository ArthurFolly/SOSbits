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

    @Transactional(readOnly = true)
    public List<Categoria> listarAtivas() {
        return repository.findByDeletadoFalseOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarDeletadas() {
        return repository.findByDeletadoTrueOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada (ID: " + id + ")"));
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        validarCategoria(categoria);

        if (categoria.getDataCriacao() == null) {
            categoria.setDataCriacao(LocalDateTime.now());
        }

        return repository.save(categoria);
    }

    @Transactional
    public Categoria criar(Categoria categoria) {
        return salvar(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, Categoria dados) {
        Categoria atual = buscarPorId(id);

        if (dados == null) {
            throw new RuntimeException("Dados inválidos.");
        }

        atual.setNome(dados.getNome());
        atual.setDescricao(dados.getDescricao());

        return salvar(atual);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);
        categoria.setDeletado(true);
        repository.save(categoria);
    }

    @Transactional
    public void restaurar(Long id) {
        Categoria categoria = buscarPorId(id);

        String nome = categoria.getNome() != null ? categoria.getNome().trim() : "";

        if (nome.isEmpty()) {
            throw new RuntimeException("Categoria inválida para restaurar.");
        }

        if (repository.existsByNomeIgnoreCaseAndDeletadoFalseAndIdNot(nome, id)) {
            throw new RuntimeException("Já existe uma categoria ativa com esse nome.");
        }

        categoria.setDeletado(false);
        repository.save(categoria);
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new RuntimeException("Categoria inválida.");
        }

        String nome = categoria.getNome() != null ? categoria.getNome().trim() : "";
        String descricao = categoria.getDescricao() != null ? categoria.getDescricao().trim() : "";

        if (nome.isEmpty()) {
            throw new RuntimeException("O nome da categoria é obrigatório.");
        }

        if (descricao.isEmpty()) {
            throw new RuntimeException("A descrição da categoria é obrigatória.");
        }

        boolean nomeDuplicado;

        if (categoria.getId() == null) {
            nomeDuplicado = repository.existsByNomeIgnoreCaseAndDeletadoFalse(nome);
        } else {
            nomeDuplicado = repository.existsByNomeIgnoreCaseAndDeletadoFalseAndIdNot(nome, categoria.getId());
        }

        if (nomeDuplicado) {
            throw new RuntimeException("Já existe uma categoria ativa com esse nome.");
        }

        categoria.setNome(nome);
        categoria.setDescricao(descricao);
    }
}
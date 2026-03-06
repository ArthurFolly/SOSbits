package com.sosbits.helpdesk.service;

import com.sosbits.helpdesk.model.Setor;
import com.sosbits.helpdesk.repository.SetorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetorService {

    private final SetorRepository repository;

    public SetorService(SetorRepository repository) {
        this.repository = repository;
    }

    public List<Setor> listarAtivos() {
        return repository.findAllByDeletadoFalseOrderByIdDesc();
    }

    public List<Setor> listarDeletados() {
        return repository.findAllByDeletadoTrueOrderByIdDesc();
    }

    public Setor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));
    }

    public Setor salvar(Setor setor) {
        validarNome(setor.getNome(), setor.getId());
        return repository.save(setor);
    }

    public Setor criar(Setor setor) {
        if (setor == null) {
            throw new RuntimeException("Dados do setor inválidos.");
        }

        setor.setId(null);
        setor.setDeletado(false);

        validarNome(setor.getNome(), null);

        return repository.save(setor);
    }

    public Setor atualizar(Long id, Setor dados) {
        Setor setor = buscarPorId(id);

        String nome = dados.getNome() != null ? dados.getNome().trim() : "";
        String descricao = dados.getDescricao() != null ? dados.getDescricao().trim() : "";

        validarNome(nome, id);

        setor.setNome(nome);
        setor.setDescricao(descricao.isBlank() ? null : descricao);

        return repository.save(setor);
    }

    public void excluir(Long id) {
        Setor setor = buscarPorId(id);
        setor.setDeletado(true);
        repository.save(setor);
    }

    public void restaurar(Long id) {
        Setor setor = buscarPorId(id);
        setor.setDeletado(false);
        repository.save(setor);
    }

    private void validarNome(String nome, Long idAtual) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RuntimeException("O nome do setor é obrigatório.");
        }

        List<Setor> lista = repository.findAll();

        for (Setor item : lista) {
            if (item.getNome() == null) continue;

            boolean mesmoNome = item.getNome().trim().equalsIgnoreCase(nome.trim());
            boolean outroId = (idAtual == null || !item.getId().equals(idAtual));

            if (mesmoNome && outroId && !item.isDeletado()) {
                throw new RuntimeException("Já existe um setor com esse nome.");
            }
        }
    }
}
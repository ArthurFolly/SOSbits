function abrirModalCategoriaCriar() {
    const modal = document.getElementById("modalCategoriaCriar");
    if (!modal) return;

    modal.classList.add("active");
    document.body.style.overflow = "hidden";
}

function fecharModalCategoriaCriar() {
    const modal = document.getElementById("modalCategoriaCriar");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}

function abrirModalCategoriasFiltros() {
    const modal = document.getElementById("modalCategoriasFiltros");
    if (!modal) return;

    modal.classList.add("active");
    document.body.style.overflow = "hidden";
}

function fecharModalCategoriasFiltros() {
    const modal = document.getElementById("modalCategoriasFiltros");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}

function abrirModalCategoriaEditar(el) {
    const id = el?.dataset?.id;
    if (!id) return;

    const modal = document.getElementById("modalCategoriaEditar");
    if (!modal) return;

    // limpa erro
    const erro = document.getElementById("catEditErro");
    if (erro) { erro.style.display = "none"; erro.textContent = ""; }

    fetch(`/categorias/api/${id}`)
        .then(r => {
            if (!r.ok) throw new Error("Falha ao buscar categoria");
            return r.json();
        })
        .then(c => {
            document.getElementById("catEditId").value = c.id;
            document.getElementById("catEditNome").value = c.nome || "";
            document.getElementById("catEditDescricao").value = c.descricao || "";

            updateCharCountCategoriaEditar(document.getElementById("catEditDescricao"));

            modal.classList.add("active");
            document.body.style.overflow = "hidden";
        })
        .catch(() => alert("Não foi possível abrir a categoria para edição."));
}

function fecharModalCategoriaEditar() {
    const modal = document.getElementById("modalCategoriaEditar");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}

function abrirModalCategoriasExcluidas() {
    const modal = document.getElementById("modalCategoriasExcluidas");
    if (!modal) return;

    modal.classList.add("active");
    document.body.style.overflow = "hidden";
    carregarCategoriasExcluidas();
}

function fecharModalCategoriasExcluidas() {
    const modal = document.getElementById("modalCategoriasExcluidas");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}
function updateCharCountCategoriaCriar(textarea) {
    const counter = document.getElementById("catCharCountCriar");
    if (counter) counter.textContent = (textarea?.value || "").length;
}

function updateCharCountCategoriaEditar(textarea) {
    const counter = document.getElementById("catCharCountEditar");
    if (counter) counter.textContent = (textarea?.value || "").length;
}

document.addEventListener("DOMContentLoaded", () => {
    const searchTop = document.getElementById("categoriasSearchInput");
    if (searchTop) {
        searchTop.addEventListener("input", () => {
            const val = (searchTop.value || "").trim().toLowerCase();
            aplicarBuscaTopoCategorias(val);
        });
    }
});

function aplicarBuscaTopoCategorias(busca) {
    const tbody = document.getElementById("categoriasTableBody");
    if (!tbody) return;

    const rows = Array.from(tbody.querySelectorAll("tr"));

    rows.forEach(row => {
        // ignora linha "Nenhuma categoria encontrada"
        if (row.querySelector("td[colspan]")) return;

        const textoLinha = (row.innerText || "").trim().toLowerCase();
        const mostrar = !busca || textoLinha.includes(busca);

        row.style.display = mostrar ? "" : "none";
    });
}

function aplicarFiltrosCategorias() {
    const busca = (document.getElementById("filtroCategoriaBusca")?.value || "").trim().toLowerCase();
    aplicarBuscaTopoCategorias(busca);
    fecharModalCategoriasFiltros();
}

function limparFiltrosCategorias() {
    const inputBusca = document.getElementById("filtroCategoriaBusca");
    if (inputBusca) inputBusca.value = "";

    const tbody = document.getElementById("categoriasTableBody");
    if (tbody) {
        tbody.querySelectorAll("tr").forEach(tr => tr.style.display = "");
    }
}

function salvarEdicaoCategoria() {
    const id = document.getElementById("catEditId")?.value;
    if (!id) return;

    const nome = (document.getElementById("catEditNome")?.value || "").trim();
    const descricao = (document.getElementById("catEditDescricao")?.value || "").trim();

    const erro = document.getElementById("catEditErro");
    if (erro) { erro.style.display = "none"; erro.textContent = ""; }

    if (!nome) {
        if (erro) {
            erro.textContent = "Preencha o Nome da categoria.";
            erro.style.display = "block";
        }
        return;
    }

    const payload = { nome, descricao };

    fetch(`/categorias/api/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
        .then(async r => {
            const data = await r.json().catch(() => ({}));
            if (!r.ok) throw new Error(data.message || "Falha ao salvar");
            return data;
        })
        .then(() => {
            // Como a tabela é Thymeleaf, recarregar é o mais seguro e simples.
            location.reload();
        })
        .catch((e) => {
            if (erro) {
                erro.textContent = e.message || "Erro ao salvar. Verifique o servidor/console.";
                erro.style.display = "block";
            }
        });
}

function carregarCategoriasExcluidas() {
    const status = document.getElementById("categoriasExcluidasStatus");
    const tbody = document.getElementById("categoriasExcluidasTable");

    if (status) status.textContent = "Carregando...";
    if (tbody) tbody.innerHTML = "";

    fetch("/categorias/api/excluidas")
        .then(r => {
            if (!r.ok) throw new Error("Falha ao listar excluídas");
            return r.json();
        })
        .then(lista => {
            if (!tbody) return;

            if (!lista || lista.length === 0) {
                if (status) status.textContent = "Nenhuma categoria excluída.";
                tbody.innerHTML = `<tr><td colspan="4" style="padding:14px;">Nenhuma categoria excluída.</td></tr>`;
                return;
            }

            if (status) status.textContent = `${lista.length} categoria(s) excluída(s).`;

            tbody.innerHTML = lista.map(c => `
                <tr data-id="${c.id}">
                    <td class="id-cell">#${c.id}</td>
                    <td>${escapeHtml(c.nome || "")}</td>
                    <td>${escapeHtml(c.descricao || "")}</td>
                    <td>
                        <button type="button" class="btn-filter-trigger"
                                onclick="restaurarCategoria(${c.id}, this)">
                            <i class="fas fa-undo"></i> Restaurar
                        </button>
                    </td>
                </tr>
            `).join("");
        })
        .catch(() => {
            if (status) status.textContent = "Erro ao carregar excluídas.";
            if (tbody) tbody.innerHTML = `<tr><td colspan="4" style="padding:14px;color:#ef4444;">Erro ao carregar.</td></tr>`;
        });
}

function restaurarCategoria(id, btn) {
    if (!confirm("Deseja restaurar esta categoria?")) return;

    if (btn) {
        btn.disabled = true;
        btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Restaurando`;
    }

    fetch(`/categorias/api/${id}/restaurar`, { method: "POST" })
        .then(r => {
            if (!r.ok) throw new Error("Falha ao restaurar");

            // remove linha e recarrega listagem do modal
            const row = document.querySelector(`#categoriasExcluidasTable tr[data-id="${id}"]`);
            if (row) row.remove();

            carregarCategoriasExcluidas();

            // recarrega a tela principal (ativos)
            location.reload();
        })
        .catch(() => {
            alert("Não foi possível restaurar. Verifique o servidor.");
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = `<i class="fas fa-undo"></i> Restaurar`;
            }
        });
}

function escapeHtml(str) {
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

document.addEventListener("click", function (event) {
    const modalCriar = document.getElementById("modalCategoriaCriar");
    if (modalCriar && modalCriar.classList.contains("active") && event.target === modalCriar) {
        fecharModalCategoriaCriar();
    }

    const modalFiltros = document.getElementById("modalCategoriasFiltros");
    if (modalFiltros && modalFiltros.classList.contains("active") && event.target === modalFiltros) {
        fecharModalCategoriasFiltros();
    }

    const modalEditar = document.getElementById("modalCategoriaEditar");
    if (modalEditar && modalEditar.classList.contains("active") && event.target === modalEditar) {
        fecharModalCategoriaEditar();
    }

    const modalExcluidas = document.getElementById("modalCategoriasExcluidas");
    if (modalExcluidas && modalExcluidas.classList.contains("active") && event.target === modalExcluidas) {
        fecharModalCategoriasExcluidas();
    }
});
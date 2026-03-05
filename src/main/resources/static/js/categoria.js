(function () {
    "use strict";


    function $(id) {
        return document.getElementById(id);
    }

    function showModal(modalId) {
        const modal = $(modalId);
        if (!modal) return;

        modal.classList.add("active");
        modal.style.display = "flex"; // garante abrir mesmo se o CSS estiver display:none
        document.body.style.overflow = "hidden";
    }

    function hideModal(modalId) {
        const modal = $(modalId);
        if (!modal) return;

        modal.classList.remove("active");
        modal.style.display = "none";
        document.body.style.overflow = "";
    }

    function hideAllModals() {
        const modals = document.querySelectorAll(".modal-overlay");
        modals.forEach(m => {
            m.classList.remove("active");
            m.style.display = "none";
        });
        document.body.style.overflow = "";
    }


    window.abrirModalCategoriasFiltros = function () {
        showModal("modalCategoriasFiltros");
        const input = $("filtroCategoriaBusca");
        if (input) input.focus();
    };

    window.fecharModalCategoriasFiltros = function () {
        hideModal("modalCategoriasFiltros");
    };

    window.abrirModalCategoriasExcluidas = function () {
        showModal("modalCategoriasExcluidas");
        carregarCategoriasExcluidas();
    };

    window.fecharModalCategoriasExcluidas = function () {
        hideModal("modalCategoriasExcluidas");
    };

    window.abrirModalCategoriaCriar = function () {
        showModal("modalCategoriaCriar");
        // zera contador ao abrir
        const span = $("catCharCountCriar");
        if (span) span.textContent = "0";
    };

    window.fecharModalCategoriaCriar = function () {
        hideModal("modalCategoriaCriar");
    };

    window.abrirModalCategoriaEditar = function (el) {
        // pega o ID do atributo data-id no botão de editar
        const id = el?.getAttribute("data-id");
        if (!id) return;

        // tenta pegar os dados direto da linha (nome/descricao) sem depender do backend
        const tr = el.closest("tr");
        const nome = tr?.querySelector(".col-nome")?.innerText?.trim() || "";
        const desc = tr?.querySelector(".col-desc")?.innerText?.trim() || "";

        const inputId = $("catEditId");
        const inputNome = $("catEditNome");
        const inputDesc = $("catEditDescricao");

        if (inputId) inputId.value = id;
        if (inputNome) inputNome.value = nome;
        if (inputDesc) inputDesc.value = (desc === "-" ? "" : desc);

        // contador
        const span = $("catCharCountEditar");
        if (span && inputDesc) span.textContent = String(inputDesc.value.length);

        // limpa erro
        const erro = $("catEditErro");
        if (erro) {
            erro.style.display = "none";
            erro.textContent = "";
        }

        showModal("modalCategoriaEditar");
        if (inputNome) inputNome.focus();
    };

    window.fecharModalCategoriaEditar = function () {
        hideModal("modalCategoriaEditar");
    };


    window.updateCharCountCategoriaCriar = function (textarea) {
        const span = $("catCharCountCriar");
        if (!span || !textarea) return;
        span.textContent = String((textarea.value || "").length);
    };

    window.updateCharCountCategoriaEditar = function (textarea) {
        const span = $("catCharCountEditar");
        if (!span || !textarea) return;
        span.textContent = String((textarea.value || "").length);
    };

    function aplicarFiltroLocal(termo) {
        const tbody = $("categoriasTableBody");
        if (!tbody) return;

        const linhas = tbody.querySelectorAll("tr");
        linhas.forEach(tr => {
            // ignora linha "Nenhuma categoria..."
            if (tr.querySelector(".empty-row")) return;

            const texto = (tr.innerText || "").toLowerCase();
            tr.style.display = !termo || texto.includes(termo) ? "" : "none";
        });
    }

    window.aplicarFiltrosCategorias = function () {
        const input = $("filtroCategoriaBusca");
        const termo = (input?.value || "").trim().toLowerCase();
        aplicarFiltroLocal(termo);
        fecharModalCategoriasFiltros();
    };

    window.limparFiltrosCategorias = function () {
        const input = $("filtroCategoriaBusca");
        if (input) input.value = "";
        aplicarFiltroLocal("");
    };

    // Barra de busca principal (em tempo real)
    function ativarBuscaPrincipal() {
        const input = $("categoriasSearchInput");
        if (!input) return;

        input.addEventListener("input", function () {
            const termo = (input.value || "").trim().toLowerCase();
            aplicarFiltroLocal(termo);
        });
    }


    window.salvarEdicaoCategoria = async function () {
        const id = $("catEditId")?.value;
        const nome = ($("catEditNome")?.value || "").trim();
        const descricao = ($("catEditDescricao")?.value || "").trim();

        const erroBox = $("catEditErro");
        const showErro = (msg) => {
            if (!erroBox) return;
            erroBox.textContent = msg;
            erroBox.style.display = "block";
        };

        if (!id) return showErro("ID inválido.");
        if (!nome) return showErro("O nome é obrigatório.");

        try {
            // Envia como x-www-form-urlencoded (mais comum no Spring MVC)
            const body = new URLSearchParams();
            body.append("id", id);
            body.append("nome", nome);
            body.append("descricao", descricao);

            const resp = await fetch("/categorias/editar", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: body.toString()
            });

            if (!resp.ok) {
                const txt = await resp.text().catch(() => "");
                return showErro("Erro ao salvar. " + (txt || ""));
            }

            // sucesso: recarrega a página para atualizar tabela
            window.location.reload();

        } catch (e) {
            showErro("Falha de rede/servidor ao salvar.");
        }
    };



    window.carregarCategoriasExcluidas = async function () {
        const tbody = $("categoriasExcluidasTable");
        const status = $("categoriasExcluidasStatus");
        if (!tbody) return;

        tbody.innerHTML = "";
        if (status) status.textContent = "Carregando...";

        try {
            const resp = await fetch("/categorias/excluidas", { method: "GET" });
            if (!resp.ok) {
                if (status) status.textContent = "Erro ao carregar excluídas.";
                return;
            }

            const lista = await resp.json();
            if (!Array.isArray(lista) || lista.length === 0) {
                if (status) status.textContent = "Nenhuma categoria excluída.";
                tbody.innerHTML = `<tr><td colspan="4" class="empty-row">Nenhuma categoria excluída.</td></tr>`;
                return;
            }

            if (status) status.textContent = `${lista.length} categoria(s) excluída(s)`;

            lista.forEach(cat => {
                const id = cat.idCategoria ?? cat.id; // aceita os dois nomes
                const nome = cat.nome ?? "-";
                const descricao = cat.descricao ?? "-";

                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="id-cell">#${id}</td>
                    <td>${escapeHtml(nome)}</td>
                    <td>${escapeHtml(descricao)}</td>
                    <td>
                        <button type="button" class="btn-ghost-elite" data-id="${id}">
                            <i class="fas fa-rotate-left"></i> Restaurar
                        </button>
                    </td>
                `;

                tr.querySelector("button")?.addEventListener("click", () => restaurarCategoria(id));
                tbody.appendChild(tr);
            });

        } catch (e) {
            if (status) status.textContent = "Falha ao carregar excluídas.";
        }
    };

    async function restaurarCategoria(id) {
        if (!id) return;

        try {
            const body = new URLSearchParams();
            body.append("id", id);

            const resp = await fetch("/categorias/restaurar", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: body.toString()
            });

            if (!resp.ok) return;

            // atualiza a lista excluídas e também a principal
            await carregarCategoriasExcluidas();
            // opcional: recarregar para aparecer na lista principal
            window.location.reload();

        } catch (e) {
            // silencioso
        }
    }

    function escapeHtml(str) {
        return String(str)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    function ativarESC() {
        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape") hideAllModals();
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        ativarBuscaPrincipal();
        ativarESC();


        hideAllModals();
    });

})();
(function () {
    "use strict";

    function $(id) {
        return document.getElementById(id);
    }

    function showModal(modalId) {
        const modal = $(modalId);
        if (!modal) return;

        modal.classList.add("active");
        modal.style.display = "flex";
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
        modals.forEach((modal) => {
            modal.classList.remove("active");
            modal.style.display = "none";
        });
        document.body.style.overflow = "";
    }

    function escapeHtml(str) {
        return String(str ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    function aplicarFiltroLocal(termo) {
        const tbody = $("setoresTableBody");
        if (!tbody) return;

        const linhas = tbody.querySelectorAll("tr");

        linhas.forEach((tr) => {
            if (tr.querySelector(".empty-row")) return;

            const textoLinha = (tr.innerText || "").toLowerCase();
            tr.style.display = !termo || textoLinha.includes(termo) ? "" : "none";
        });
    }

    function ativarBuscaPrincipal() {
        const input = $("setoresSearchInput");
        if (!input) return;

        input.addEventListener("input", function () {
            const termo = (input.value || "").trim().toLowerCase();
            aplicarFiltroLocal(termo);
        });
    }

    function ativarESC() {
        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape") {
                hideAllModals();
            }
        });
    }

    function ativarCliqueForaModal() {
        document.querySelectorAll(".modal-overlay").forEach((overlay) => {
            overlay.addEventListener("click", function (e) {
                if (e.target === overlay) {
                    hideAllModals();
                }
            });
        });
    }

    // =========================================================
    // MODAL FILTROS
    // =========================================================

    window.abrirModalSetoresFiltros = function () {
        showModal("modalSetoresFiltros");
        const input = $("filtroSetorBusca");
        if (input) input.focus();
    };

    window.fecharModalSetoresFiltros = function () {
        hideModal("modalSetoresFiltros");
    };

    window.aplicarFiltrosSetores = function () {
        const input = $("filtroSetorBusca");
        const termo = (input?.value || "").trim().toLowerCase();
        aplicarFiltroLocal(termo);
        hideModal("modalSetoresFiltros");
    };

    window.limparFiltrosSetores = function () {
        const input = $("filtroSetorBusca");
        if (input) input.value = "";
        aplicarFiltroLocal("");
    };

    // =========================================================
    // MODAL CRIAR
    // =========================================================

    window.abrirModalSetorCriar = function () {
        showModal("modalSetorCriar");

        const span = $("setorCharCountCriar");
        if (span) span.textContent = "0";

        const modal = $("modalSetorCriar");
        const inputNome = modal?.querySelector('input[name="nome"]');
        if (inputNome) inputNome.focus();
    };

    window.fecharModalSetorCriar = function () {
        hideModal("modalSetorCriar");
    };

    window.updateCharCountSetorCriar = function (textarea) {
        const span = $("setorCharCountCriar");
        if (!span || !textarea) return;
        span.textContent = String((textarea.value || "").length);
    };

    // =========================================================
    // MODAL EDITAR
    // =========================================================

    window.abrirModalSetorEditar = function (el) {
        const id = el?.getAttribute("data-id");
        if (!id) return;

        const tr = el.closest("tr");
        const nome = tr?.querySelector(".col-nome")?.innerText?.trim() || "";
        const descricao = tr?.querySelector(".col-desc")?.innerText?.trim() || "";

        const inputId = $("setorEditId");
        const inputNome = $("setorEditNome");
        const inputDescricao = $("setorEditDescricao");
        const erro = $("setorEditErro");
        const contador = $("setorCharCountEditar");

        if (inputId) inputId.value = id;
        if (inputNome) inputNome.value = nome;
        if (inputDescricao) inputDescricao.value = (descricao === "-" ? "" : descricao);

        if (contador && inputDescricao) {
            contador.textContent = String(inputDescricao.value.length);
        }

        if (erro) {
            erro.style.display = "none";
            erro.textContent = "";
        }

        showModal("modalSetorEditar");
        if (inputNome) inputNome.focus();
    };

    window.fecharModalSetorEditar = function () {
        hideModal("modalSetorEditar");
    };

    window.updateCharCountSetorEditar = function (textarea) {
        const span = $("setorCharCountEditar");
        if (!span || !textarea) return;
        span.textContent = String((textarea.value || "").length);
    };

    window.salvarEdicaoSetor = async function () {
        const id = $("setorEditId")?.value;
        const nome = ($("setorEditNome")?.value || "").trim();
        const descricao = ($("setorEditDescricao")?.value || "").trim();

        const erroBox = $("setorEditErro");

        function showErro(msg) {
            if (!erroBox) return;
            erroBox.textContent = msg;
            erroBox.style.display = "block";
        }

        if (!id) {
            showErro("ID inválido.");
            return;
        }

        if (!nome) {
            showErro("O nome do setor é obrigatório.");
            return;
        }

        try {
            const body = new URLSearchParams();
            body.append("id", id);
            body.append("nome", nome);
            body.append("descricao", descricao);

            const resp = await fetch("/setores/editar", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: body.toString()
            });

            if (!resp.ok) {
                const txt = await resp.text().catch(() => "");
                showErro("Erro ao salvar. " + (txt || ""));
                return;
            }

            window.location.reload();

        } catch (e) {
            showErro("Falha de rede/servidor ao salvar.");
        }
    };

    // =========================================================
    // MODAL EXCLUÍDOS
    // =========================================================

    window.abrirModalSetoresExcluidos = function () {
        showModal("modalSetoresExcluidos");
        window.carregarSetoresExcluidos();
    };

    window.fecharModalSetoresExcluidos = function () {
        hideModal("modalSetoresExcluidos");
    };

    window.carregarSetoresExcluidos = async function () {
        const tbody = $("setoresExcluidosTable");
        const status = $("setoresExcluidosStatus");

        if (!tbody) return;

        tbody.innerHTML = "";
        if (status) status.textContent = "Carregando...";

        try {
            const resp = await fetch("/setores/excluidos", { method: "GET" });

            if (!resp.ok) {
                if (status) status.textContent = "Erro ao carregar excluídos.";
                tbody.innerHTML = `
                    <tr>
                        <td colspan="4" class="empty-row">Erro ao carregar setores excluídos.</td>
                    </tr>
                `;
                return;
            }

            const lista = await resp.json();

            if (!Array.isArray(lista) || lista.length === 0) {
                if (status) status.textContent = "Nenhum setor excluído.";
                tbody.innerHTML = `
                    <tr>
                        <td colspan="4" class="empty-row">Nenhum setor excluído.</td>
                    </tr>
                `;
                return;
            }

            if (status) {
                status.textContent = `${lista.length} setor(es) excluído(s)`;
            }

            lista.forEach((setor) => {
                const id = setor.id ?? "";
                const nome = setor.nome ?? "-";
                const descricao = setor.descricao ?? "-";

                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="id-cell">#${escapeHtml(id)}</td>
                    <td>${escapeHtml(nome)}</td>
                    <td>${escapeHtml(descricao)}</td>
                    <td>
                        <button type="button" class="btn-ghost-elite btn-restaurar-setor" data-id="${escapeHtml(id)}">
                            <i class="fas fa-rotate-left"></i> Restaurar
                        </button>
                    </td>
                `;

                const btn = tr.querySelector(".btn-restaurar-setor");
                if (btn) {
                    btn.addEventListener("click", function () {
                        restaurarSetor(id);
                    });
                }

                tbody.appendChild(tr);
            });

        } catch (e) {
            if (status) status.textContent = "Falha ao carregar excluídos.";
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-row">Falha ao carregar setores excluídos.</td>
                </tr>
            `;
        }
    };

    async function restaurarSetor(id) {
        if (!id) return;

        try {
            const body = new URLSearchParams();
            body.append("id", id);

            const resp = await fetch("/setores/restaurar", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body: body.toString()
            });

            if (!resp.ok) return;

            await window.carregarSetoresExcluidos();
            window.location.reload();

        } catch (e) {
            console.error("Erro ao restaurar setor:", e);
        }
    }

    // =========================================================
    // INIT
    // =========================================================

    document.addEventListener("DOMContentLoaded", function () {
        ativarBuscaPrincipal();
        ativarESC();
        ativarCliqueForaModal();
        hideAllModals();
    });

})();
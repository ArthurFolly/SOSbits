/* =========================================================
   usuario.js - EXCLUSIVO da tela de Usuários (SEM FETCH)

   O que ele faz:
   1) Busca (filtra tabela localmente)
   2) Abre/fecha modais (Novo Usuário / Filtros / Excluídos)
   3) Fecha modal clicando fora (overlay)
   4) Fecha modal com tecla ESC
========================================================= */

(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        ativarBuscaUsuarios();
        ativarCliqueForaParaFecharModais();
        ativarTeclaESCParaFecharModais();
    });

    // =========================================================
    // 1) BUSCA: filtra as linhas da tabela conforme texto digitado
    // Requisito no HTML:
    // - input:  id="usuariosSearchInput"
    // - tbody:  id="usuariosTableBody"
    // =========================================================
    function ativarBuscaUsuarios() {
        const input = document.getElementById("usuariosSearchInput");
        const tbody = document.getElementById("usuariosTableBody");

        if (!input || !tbody) return;

        input.addEventListener("input", function () {
            const termo = (input.value || "").trim().toLowerCase();
            const linhas = tbody.querySelectorAll("tr");

            linhas.forEach((tr) => {
                const textoLinha = (tr.innerText || "").toLowerCase();
                tr.style.display = !termo || textoLinha.includes(termo) ? "" : "none";
            });
        });
    }

    // =========================================================
    // 2) MODAL GENÉRICO: abrir/fechar por ID
    // Você chama no HTML:
    // - abrirModalGenerico('modalNovoUsuario')
    // - fecharModalGenerico('modalNovoUsuario')
    // =========================================================
    window.abrirModalGenerico = function (modalId) {
        const overlay = document.getElementById(modalId);
        if (!overlay) return;

        overlay.classList.add("active");
        overlay.style.display = "flex";
        document.body.style.overflow = "hidden";
    };

    window.fecharModalGenerico = function (modalId) {
        const overlay = document.getElementById(modalId);
        if (!overlay) return;

        overlay.classList.remove("active");
        overlay.style.display = "none";

        // só libera scroll se não existir nenhum outro modal ativo
        const aindaTemModalAberto = document.querySelector(".modal-overlay.active");
        if (!aindaTemModalAberto) {
            document.body.style.overflow = "";
        }
    };

    // =========================================================
    // 3) MODAL EXCLUÍDOS (SEM FETCH)
    // IMPORTANTE:
    // - NÃO mexer no tbody, porque o Thymeleaf já renderizou no HTML
    // =========================================================
    window.abrirModalUsuariosExcluidos = function () {
        abrirModalGenerico("modalUsuariosExcluidos");
    };

    window.fecharModalUsuariosExcluidos = function () {
        fecharModalGenerico("modalUsuariosExcluidos");
    };

    // =========================================================
    // 4) FECHAR MODAL: clique fora (overlay)
    // Fecha qualquer .modal-overlay que esteja .active
    // =========================================================
    function ativarCliqueForaParaFecharModais() {
        document.addEventListener("click", function (event) {
            const overlay = event.target;

            // se clicou no próprio overlay (fundo), fecha
            if (overlay && overlay.classList && overlay.classList.contains("modal-overlay")) {
                if (overlay.classList.contains("active")) {
                    fecharModalGenerico(overlay.id);
                }
            }
        });
    }

    // =========================================================
    // 5) FECHAR MODAL: tecla ESC
    // =========================================================
    function ativarTeclaESCParaFecharModais() {
        document.addEventListener("keydown", function (e) {
            if (e.key !== "Escape") return;

            document.querySelectorAll(".modal-overlay.active").forEach((overlay) => {
                fecharModalGenerico(overlay.id);
            });
        });
    }
})();

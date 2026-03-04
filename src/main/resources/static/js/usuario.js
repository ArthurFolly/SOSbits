(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        ativarBuscaUsuarios();
        ativarCliqueForaParaFecharModais();
        ativarTeclaESCParaFecharModais();
    });

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
    window.abrirModalUsuariosExcluidos = function () {
        abrirModalGenerico("modalUsuariosExcluidos");
    };

    window.fecharModalUsuariosExcluidos = function () {
        fecharModalGenerico("modalUsuariosExcluidos");
    };

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

    function ativarTeclaESCParaFecharModais() {
        document.addEventListener("keydown", function (e) {
            if (e.key !== "Escape") return;

            document.querySelectorAll(".modal-overlay.active").forEach((overlay) => {
                fecharModalGenerico(overlay.id);
            });
        });
    }
})();

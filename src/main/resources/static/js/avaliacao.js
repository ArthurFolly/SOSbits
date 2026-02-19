
function abrirModalGenerico(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.style.display = "flex";
    document.body.classList.add("modal-open");
}

function fecharModalGenerico(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.style.display = "none";
    document.body.classList.remove("modal-open");
}


document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("avaliacoesSearchInput");
    const tbody = document.getElementById("avaliacoesTableBody");

    if (!input || !tbody) return;

    input.addEventListener("input", () => {
        const termo = (input.value || "").trim().toLowerCase();

        const linhas = tbody.querySelectorAll("tr");
        linhas.forEach((tr) => {
            // Ignora linha "Nenhuma avaliação encontrada"
            if (tr.querySelector("td[colspan]")) return;

            const textoLinha = (tr.innerText || "").toLowerCase();
            tr.style.display = textoLinha.includes(termo) ? "" : "none";
        });
    });


    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") {
            // fecha qualquer modal overlay visível
            document.querySelectorAll(".modal-overlay").forEach((m) => {
                if (m.style.display === "flex") m.style.display = "none";
            });
            document.body.classList.remove("modal-open");
        }
    });
});

function abrirModalDetalheAvaliacao(btn) {
    // btn é o botão que foi clicado
    const tr = btn.closest("tr");
    if (!tr) return;


    const tds = tr.querySelectorAll("td");
    if (!tds || tds.length < 6) return;

    const id = (tds[0].innerText || "").trim();
    const chamado = (tds[1].innerText || "").trim();
    const avaliador = (tds[2].innerText || "").trim();
    const nota = (tds[3].innerText || "").trim();


    const comentario = (btn.getAttribute("data-comentario") || "—").trim();

    setText("detAvaliacaoId", id || "-");
    setText("detChamado", chamado || "-");
    setText("detAvaliador", avaliador || "-");
    setText("detNota", nota || "-");
    setText("detComentario", comentario || "—");

    abrirModalGenerico("modalDetalheAvaliacao");
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
}

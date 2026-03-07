/* =========================================================
   MODAIS GENÉRICOS
   ========================================================= */

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

/* =========================================================
   ABERTURA DOS MODAIS PRINCIPAIS
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {
    const btnExcluidas = document.getElementById("btnAbrirExcluidas");
    const btnNova = document.getElementById("btnAbrirNovaAvaliacao");
    const inputBusca = document.getElementById("avaliacoesSearchInput");
    const tabelaBody = document.getElementById("avaliacoesTableBody");

    if (btnExcluidas) {
        btnExcluidas.addEventListener("click", () => {
            abrirModalGenerico("modalAvaliacoesExcluidas");
        });
    }

    if (btnNova) {
        btnNova.addEventListener("click", () => {
            abrirModalGenerico("modalNovaAvaliacao");
        });
    }

    if (inputBusca && tabelaBody) {
        inputBusca.addEventListener("input", () => {
            const termo = inputBusca.value.toLowerCase();
            const linhas = tabelaBody.querySelectorAll("tr");

            linhas.forEach(linha => {
                const textoLinha = linha.innerText.toLowerCase();
                linha.style.display = textoLinha.includes(termo) ? "" : "none";
            });
        });
    }

    document.querySelectorAll(".modal-overlay").forEach(modal => {
        modal.addEventListener("click", (event) => {
            if (event.target === modal) {
                modal.style.display = "none";
                document.body.classList.remove("modal-open");
            }
        });
    });
});

/* =========================================================
   FECHAR COM ESC
   ========================================================= */

document.addEventListener("keydown", (e) => {
    if (e.key !== "Escape") return;

    document.querySelectorAll(".modal-overlay").forEach(modal => {
        if (modal.style.display === "flex") {
            modal.style.display = "none";
        }
    });

    document.body.classList.remove("modal-open");
});

/* =========================================================
   MODAL DETALHE
   ========================================================= */

function abrirModalDetalheAvaliacao(botao) {
    const id = botao.getAttribute("data-id") || "-";
    const chamado = botao.getAttribute("data-chamado") || "-";
    const avaliador = botao.getAttribute("data-avaliador") || "-";
    const nota = parseInt(botao.getAttribute("data-nota") || "0", 10);
    const status = botao.getAttribute("data-status") || "-";
    const data = botao.getAttribute("data-data") || "-";
    const comentario = botao.getAttribute("data-comentario") || "—";

    setText("detAvaliacaoId", id);
    setText("detChamado", chamado);
    setText("detAvaliador", avaliador);
    setText("detStatus", status);
    setText("detData", data);
    setText("detComentario", comentario);

    renderStars(nota);

    abrirModalGenerico("modalDetalheAvaliacao");
}

/* =========================================================
   MODAL EDITAR
   ========================================================= */

function abrirModalEditarAvaliacao(botao) {
    const id = botao.getAttribute("data-id") || "";
    const nota = botao.getAttribute("data-nota") || "";
    const comentario = botao.getAttribute("data-comentario") || "";

    const campoId = document.getElementById("editAvaliacaoId");
    const campoNota = document.getElementById("editNota");
    const campoComentario = document.getElementById("editComentario");

    if (campoId) campoId.value = id;
    if (campoNota) campoNota.value = nota;
    if (campoComentario) campoComentario.value = comentario;

    abrirModalGenerico("modalEditarAvaliacao");
}

/* =========================================================
   AUXILIAR
   ========================================================= */

function setText(idElemento, valor) {
    const elemento = document.getElementById(idElemento);
    if (!elemento) return;
    elemento.textContent = valor;
}

/* =========================================================
   ESTRELAS
   ========================================================= */

function renderStars(nota) {
    const container = document.getElementById("detNota");
    if (!container) return;

    container.innerHTML = "";

    for (let i = 1; i <= 5; i++) {
        const estrela = document.createElement("i");
        estrela.className = i <= nota
            ? "fas fa-star estrela-on"
            : "far fa-star estrela-off";

        container.appendChild(estrela);
    }

    const textoNota = document.createElement("span");
    textoNota.className = "nota-texto";
    textoNota.innerText = ` (${nota})`;

    container.appendChild(textoNota);
}
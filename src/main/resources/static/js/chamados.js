/* =========================================================
   MODAL — CHAMADO
   ========================================================= */

function abrirModal() {
    const modal = document.getElementById("modalChamado");
    if (!modal) return;

    modal.classList.add("active");
    document.body.style.overflow = "hidden";
}

function fecharModal() {
    const modal = document.getElementById("modalChamado");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}


/* =========================================================
   MODAL — FILTROS
   ========================================================= */

function abrirModalFiltros() {
    const modal = document.getElementById("modalFiltros");
    if (!modal) return;

    modal.classList.add("active");
    document.body.style.overflow = "hidden";
}

function fecharModalFiltros() {
    const modal = document.getElementById("modalFiltros");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}


/* =========================================================
   FECHAR MODAL AO CLICAR FORA (CORRIGIDO)
   ========================================================= */

document.addEventListener("click", function (event) {

    const modalChamado = document.getElementById("modalChamado");
    if (
        modalChamado &&
        modalChamado.classList.contains("active") &&
        event.target === modalChamado
    ) {
        fecharModal();
    }

    const modalFiltros = document.getElementById("modalFiltros");
    if (
        modalFiltros &&
        modalFiltros.classList.contains("active") &&
        event.target === modalFiltros
    ) {
        fecharModalFiltros();
    }

});


/* =========================================================
   PRIORIDADE — CHAMADO
   ========================================================= */

function setElitePriority(button, prioridade) {

    document.querySelectorAll(".prio-card-elite").forEach(card => {
        card.classList.remove("active");
    });

    button.classList.add("active");

    const input = document.getElementById("prioridadeInput");
    if (input) input.value = prioridade;

    const priorityLine = document.getElementById("priorityLine");
    if (!priorityLine) return;

    priorityLine.className = "priority-indicator-elite";

    if (prioridade === "Baixa") priorityLine.classList.add("low");
    if (prioridade === "Média") priorityLine.classList.add("med");
    if (prioridade === "Alta") priorityLine.classList.add("high");
}


/* =========================================================
   CONTADOR DE CARACTERES
   ========================================================= */

function updateCharCount(textarea) {
    const counter = document.getElementById("charCount");
    if (counter) counter.textContent = textarea.value.length;
}


/* =========================================================
   INPUT FILE
   ========================================================= */

function handleFileSelection() {
    const input = document.getElementById("fileInput");
    const text = document.getElementById("fileNameText");

    if (!input || !text) return;

    text.textContent = input.files.length
        ? input.files[0].name
        : "Arraste ou clique para anexar";
}


/* =========================================================
   FILTROS (VISUAL)
   ========================================================= */

function aplicarFiltrosElite() {

    const busca = document.getElementById("filterBusca").value.toLowerCase();
    const dataFiltro = document.getElementById("filterData").value;

    const statusChip = document.querySelector("#statusFilterGroup .chip.active");
    const prioChip = document.querySelector("#prioFilterGroup .chip.active");

    const status = statusChip ? statusChip.innerText : "Tudo";
    const prioridade = prioChip ? prioChip.innerText : "Tudo";

    document.querySelectorAll(".chamado-row").forEach(row => {

        const rowStatus = row.dataset.status;
        const rowPrioridade = row.dataset.prioridade;
        const rowTexto = row.dataset.texto;

        let mostrar = true;

        // STATUS
        if (status !== "Tudo" && rowStatus !== status) {
            mostrar = false;
        }

        // PRIORIDADE
        if (prioridade !== "Tudo" && rowPrioridade !== prioridade) {
            mostrar = false;
        }

        // BUSCA
        if (busca && !rowTexto.includes(busca)) {
            mostrar = false;
        }

        row.style.display = mostrar ? "" : "none";
    });

    fecharModalFiltros();
}


/* =========================================================
   CHIP FILTER (STATUS / PRIORIDADE)
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {

    document.querySelectorAll(".chip-group").forEach(group => {

        group.querySelectorAll(".chip").forEach(chip => {

            chip.addEventListener("click", () => {

                // remove active de todos do grupo
                group.querySelectorAll(".chip").forEach(c =>
                    c.classList.remove("active")
                );

                // ativa o clicado
                chip.classList.add("active");
            });

        });

    });

});

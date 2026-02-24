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

    if (input && tbody) {
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
    }

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

document.addEventListener("DOMContentLoaded", () => {
    const select = document.getElementById("selectChamadoAvaliacao");
    const previewBox = document.getElementById("previewChamadoBox");
    const pvErro = document.getElementById("pvErro");

    if (!select) return;

    select.addEventListener("change", async () => {
        const id = (select.value || "").trim();

        // reset visual
        if (pvErro) {
            pvErro.style.display = "none";
            pvErro.innerText = "";
        }
        if (previewBox) previewBox.style.display = "none";

        if (!id) return;

        try {
            const resp = await fetch(`/avaliacoes/chamados/${id}/resumo`, {
                headers: { "Accept": "application/json" }
            });

            if (!resp.ok) throw new Error("Falha ao buscar resumo.");

            const data = await resp.json();

            const elTitulo = document.getElementById("pvTitulo");
            const elStatus = document.getElementById("pvStatus");
            const elTipo = document.getElementById("pvTipo");
            const elPri = document.getElementById("pvPrioridade");
            const elAt = document.getElementById("pvAtendente");
            const elDesc = document.getElementById("pvDescricao");

            if (elTitulo) elTitulo.innerText = `#${data.id} - ${data.titulo ?? "-"}`;
            if (elStatus) elStatus.innerText = data.status ?? "-";
            if (elTipo) elTipo.innerText = data.tipo ?? "-";
            if (elPri) elPri.innerText = data.prioridade ?? "-";
            if (elAt) elAt.innerText = data.nomeAtendente ?? "Não atribuído";
            if (elDesc) elDesc.innerText = data.descricao ?? "-";

            if (previewBox) previewBox.style.display = "block";
        } catch (e) {

            if (pvErro) {
                pvErro.innerText =
                    "Não foi possível carregar os dados do chamado (pode já ter sido avaliado ou você não tem permissão).";
                pvErro.style.display = "block";
            }
            if (previewBox) previewBox.style.display = "block";
        }
    });
});
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
function abrirModalEditar(el) {
    const id = el?.dataset?.id;
    if (!id) return;

    const modal = document.getElementById("modalEditar");
    if (!modal) return;

    // limpa erro
    const erro = document.getElementById("editErro");
    if (erro) { erro.style.display = "none"; erro.textContent = ""; }

    fetch(`/chamados/${id}`)
        .then(r => {
            if (!r.ok) throw new Error("Falha ao buscar chamado");
            return r.json();
        })
        .then(c => {
            document.getElementById("editId").value = c.id;
            document.getElementById("editTipo").value = c.tipo || "";
            document.getElementById("editTitulo").value = c.titulo || "";
            document.getElementById("editDescricao").value = c.descricao || "";
            document.getElementById("editStatus").value = c.status || "Aberto";
            document.getElementById("editPrioridade").value = c.prioridade || "Baixa";

            updateCharCountEditar(document.getElementById("editDescricao"));

            modal.classList.add("active");
            document.body.style.overflow = "hidden";
        })
        .catch(() => alert("Não foi possível abrir o chamado para edição."));
}

function fecharModalEditar() {
    const modal = document.getElementById("modalEditar");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}

function updateCharCountEditar(textarea) {
    const counter = document.getElementById("charCountEditar");
    if (counter) counter.textContent = (textarea?.value || "").length;
}

function salvarEdicao() {
    const id = document.getElementById("editId").value;
    if (!id) return;

    const payload = {
        tipo: document.getElementById("editTipo").value.trim(),
        titulo: document.getElementById("editTitulo").value.trim(),
        descricao: document.getElementById("editDescricao").value.trim(),
        status: document.getElementById("editStatus").value,
        prioridade: document.getElementById("editPrioridade").value
    };

    if (!payload.tipo || !payload.titulo || !payload.descricao) {
        const erro = document.getElementById("editErro");
        if (erro) {
            erro.textContent = "Preencha Tipo, Assunto e Descrição.";
            erro.style.display = "block";
        }
        return;
    }

    fetch(`/chamados/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
        .then(r => {
            if (!r.ok) throw new Error("Falha ao salvar");
            return r.json();
        })
        .then(updated => {
            // atualiza a linha na tabela (sem recarregar)
            const row = document.querySelector(`#chamadosTable tr[data-id="${updated.id}"]`);
            if (row) {
                const tdTipo = row.querySelector(".col-tipo");
                const tdTitulo = row.querySelector(".col-titulo");
                const badgeStatus = row.querySelector(".col-status .badge");
                const badgePrio = row.querySelector(".col-prio .badge");

                if (tdTipo) tdTipo.textContent = updated.tipo || "";
                if (tdTitulo) tdTitulo.textContent = updated.titulo || "";

                if (badgeStatus) {
                    badgeStatus.textContent = updated.status || "";
                    badgeStatus.classList.remove("badge-aberto", "badge-pendente");
                    badgeStatus.classList.add(updated.status === "Aberto" ? "badge-aberto" : "badge-pendente");
                }

                if (badgePrio) {
                    badgePrio.textContent = updated.prioridade || "";
                    badgePrio.classList.remove("prio-alta", "prio-media", "prio-baixa");
                    if (updated.prioridade === "Alta") badgePrio.classList.add("prio-alta");
                    else if (updated.prioridade === "Média") badgePrio.classList.add("prio-media");
                    else badgePrio.classList.add("prio-baixa");
                }
            }

            fecharModalEditar();
        })
        .catch(() => {
            const erro = document.getElementById("editErro");
            if (erro) {
                erro.textContent = "Erro ao salvar. Verifique o servidor/console.";
                erro.style.display = "block";
            }
        });
}

function abrirModalExcluidos() {
    const modal = document.getElementById("modalExcluidos");
    if (!modal) return;

    modal.classList.add("active");
    document.body.style.overflow = "hidden";
    carregarExcluidos();
}

function fecharModalExcluidos() {
    const modal = document.getElementById("modalExcluidos");
    if (!modal) return;

    modal.classList.remove("active");
    document.body.style.overflow = "";
}

function carregarExcluidos() {
    const status = document.getElementById("excluidosStatus");
    const tbody = document.getElementById("excluidosTable");

    if (status) status.textContent = "Carregando...";
    if (tbody) tbody.innerHTML = "";

    fetch("/chamados/api?deleted=1")
        .then(r => {
            if (!r.ok) throw new Error("Falha ao listar excluídos");
            return r.json();
        })
        .then(lista => {
            if (!tbody) return;

            if (!lista || lista.length === 0) {
                if (status) status.textContent = "Nenhum chamado excluído.";
                tbody.innerHTML = `<tr><td colspan="6" style="padding:14px;">Nenhum chamado excluído.</td></tr>`;
                return;
            }

            if (status) status.textContent = `${lista.length} chamado(s) excluído(s).`;

            tbody.innerHTML = lista.map(c => `
                <tr data-id="${c.id}">
                    <td class="id-cell">#${c.id}</td>
                    <td>${escapeHtml(c.tipo || "")}</td>
                    <td>${escapeHtml(c.titulo || "")}</td>
                    <td>${escapeHtml(c.status || "")}</td>
                    <td>${escapeHtml(c.prioridade || "")}</td>
                    <td>
                        <button type="button" class="btn-filter-trigger"
                                onclick="restaurarChamado(${c.id}, this)">
                            <i class="fas fa-undo"></i> Restaurar
                        </button>
                    </td>
                </tr>
            `).join("");
        })
        .catch(() => {
            if (status) status.textContent = "Erro ao carregar excluídos.";
            if (tbody) tbody.innerHTML = `<tr><td colspan="6" style="padding:14px;color:#ef4444;">Erro ao carregar.</td></tr>`;
        });
}

function restaurarChamado(id, btn) {
    if (!confirm("Deseja restaurar este chamado?")) return;

    if (btn) {
        btn.disabled = true;
        btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Restaurando`;
    }

    fetch(`/chamados/${id}/restaurar`, { method: "PATCH" })
        .then(r => {
            if (!r.ok) throw new Error("Falha ao restaurar");
            const row = document.querySelector(`#excluidosTable tr[data-id="${id}"]`);
            if (row) row.remove();
            carregarExcluidos();

            // tabela principal é Thymeleaf -> recarrega pra voltar pros ativos
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

    const modalChamado = document.getElementById("modalChamado");
    if (modalChamado && modalChamado.classList.contains("active") && event.target === modalChamado) {
        fecharModal();
    }

    const modalFiltros = document.getElementById("modalFiltros");
    if (modalFiltros && modalFiltros.classList.contains("active") && event.target === modalFiltros) {
        fecharModalFiltros();
    }

    const modalEditar = document.getElementById("modalEditar");
    if (modalEditar && modalEditar.classList.contains("active") && event.target === modalEditar) {
        fecharModalEditar();
    }

    const modalExcluidos = document.getElementById("modalExcluidos");
    if (modalExcluidos && modalExcluidos.classList.contains("active") && event.target === modalExcluidos) {
        fecharModalExcluidos();
    }
});
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
function updateCharCount(textarea) {
    const counter = document.getElementById("charCount");
    if (counter) counter.textContent = textarea.value.length;
}
function handleFileSelection() {
    const input = document.getElementById("fileInput");
    const text = document.getElementById("fileNameText");

    if (!input || !text) return;

    text.textContent = input.files.length
        ? input.files[0].name
        : "Arraste ou clique para anexar";
}

function aplicarFiltrosElite() {
    const busca = (document.getElementById("filterBusca")?.value || "").trim().toLowerCase();
    const dataFiltroISO = (document.getElementById("filterData")?.value || "").trim(); // yyyy-mm-dd

    const statusChip = document.querySelector("#statusFilterGroup .chip.active");
    const prioChip = document.querySelector("#prioFilterGroup .chip.active");

    const statusSelecionado = statusChip ? statusChip.innerText.trim() : "Tudo";
    const prioSelecionada = prioChip ? prioChip.innerText.trim() : "Tudo";

    const tbody = document.getElementById("chamadosTable");
    if (!tbody) return;

    const rows = Array.from(tbody.querySelectorAll("tr"));

    rows.forEach(row => {
        const tds = row.querySelectorAll("td");
        if (tds.length < 6) return;

        const idTxt = (tds[0]?.innerText || "").trim().toLowerCase();
        const tipoTxt = (tds[1]?.innerText || "").trim().toLowerCase();
        const assuntoTxt = (tds[2]?.innerText || "").trim().toLowerCase();

        const statusTxt = (tds[3]?.innerText || "").trim();       // ex: "Aberto"
        const prioridadeTxt = (tds[4]?.innerText || "").trim();    // ex: "Média"

        const dataBr = (tds[5]?.innerText || "").trim();           // ex: "28/01/2026"
        const dataISO = brDateToISO(dataBr);                       // "2026-01-28"

        let mostrar = true;

        if (statusSelecionado !== "Tudo" && statusTxt !== statusSelecionado) mostrar = false;
        if (prioSelecionada !== "Tudo" && prioridadeTxt !== prioSelecionada) mostrar = false;
        if (dataFiltroISO && dataISO !== dataFiltroISO) mostrar = false;

        if (busca) {
            const textoLinha = `${idTxt} ${tipoTxt} ${assuntoTxt}`;
            if (!textoLinha.includes(busca)) mostrar = false;
        }

        row.style.display = mostrar ? "" : "none";
    });

    fecharModalFiltros();
}

function brDateToISO(br) {
    const s = (br || "").trim();
    const m = s.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
    if (!m) return "";
    const dd = m[1];
    const mm = m[2];
    const yyyy = m[3];
    return `${yyyy}-${mm}-${dd}`;
}
function limparFiltros() {
    const inputBusca = document.getElementById("filterBusca");
    if (inputBusca) inputBusca.value = "";

    const inputData = document.getElementById("filterData");
    if (inputData) inputData.value = "";

    setChipTudoAtivo("statusFilterGroup");
    setChipTudoAtivo("prioFilterGroup");

    const tbody = document.getElementById("chamadosTable");
    if (tbody) {
        tbody.querySelectorAll("tr").forEach(tr => tr.style.display = "");
    }
}

function setChipTudoAtivo(groupId) {
    const group = document.getElementById(groupId);
    if (!group) return;

    const chips = Array.from(group.querySelectorAll(".chip"));
    chips.forEach(c => c.classList.remove("active"));

    const chipTudo = chips.find(c => c.innerText.trim() === "Tudo");
    if (chipTudo) chipTudo.classList.add("active");
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".chip-group").forEach(group => {
        group.querySelectorAll(".chip").forEach(chip => {
            chip.addEventListener("click", () => {
                group.querySelectorAll(".chip").forEach(c => c.classList.remove("active"));
                chip.classList.add("active");
            });
        });
    });

    const searchTop = document.getElementById("searchInput");
    if (searchTop) {
        searchTop.addEventListener("input", () => {
            const val = (searchTop.value || "").trim().toLowerCase();
            aplicarBuscaTopo(val);
        });
    }
});

function aplicarBuscaTopo(busca) {
    const tbody = document.getElementById("chamadosTable");
    if (!tbody) return;

    const rows = Array.from(tbody.querySelectorAll("tr"));
    rows.forEach(row => {
        const tds = row.querySelectorAll("td");
        if (tds.length < 3) return;

        const idTxt = (tds[0]?.innerText || "").trim().toLowerCase();
        const tipoTxt = (tds[1]?.innerText || "").trim().toLowerCase();
        const assuntoTxt = (tds[2]?.innerText || "").trim().toLowerCase();

        const textoLinha = `${idTxt} ${tipoTxt} ${assuntoTxt}`;
        const mostrar = !busca || textoLinha.includes(busca);

        row.style.display = mostrar ? "" : "none";
    });
}

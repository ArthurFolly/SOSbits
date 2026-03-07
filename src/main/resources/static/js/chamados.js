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

    const erro = document.getElementById("editErro");
    if (erro) {
        erro.style.display = "none";
        erro.textContent = "";
    }

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
            document.getElementById("editStatus").value = normalizarStatusParaSelect(c.status);
            document.getElementById("editPrioridade").value = normalizarPrioridadeParaSelect(c.prioridade);
            document.getElementById("editSetor").value = c.setor && c.setor.id ? c.setor.id : "";

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

    const setorId = document.getElementById("editSetor").value;

    const payload = {
        tipo: document.getElementById("editTipo").value.trim(),
        titulo: document.getElementById("editTitulo").value.trim(),
        descricao: document.getElementById("editDescricao").value.trim(),
        status: normalizarStatusParaBackend(document.getElementById("editStatus").value),
        prioridade: normalizarPrioridadeParaBackend(document.getElementById("editPrioridade").value),
        setor: setorId ? { id: Number(setorId) } : null
    };

    if (!payload.tipo || !payload.titulo || !payload.descricao || !payload.setor) {
        const erro = document.getElementById("editErro");
        if (erro) {
            erro.textContent = "Preencha Tipo, Setor, Assunto e Descrição.";
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
            atualizarLinhaChamado(updated);
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

function atualizarLinhaChamado(updated) {
    const row = document.querySelector(`#chamadosTable tr[data-id="${updated.id}"]`);
    if (!row) return;

    const tdTipo = row.querySelector(".col-tipo");
    const tdSetor = row.querySelector(".col-setor");
    const tdTitulo = row.querySelector(".col-titulo");
    const tdStatus = row.querySelector(".col-status");
    const tdPrio = row.querySelector(".col-prio");

    if (tdTipo) tdTipo.textContent = updated.tipo || "";
    if (tdSetor) tdSetor.textContent = updated.setor && updated.setor.nome ? updated.setor.nome : "-";
    if (tdTitulo) tdTitulo.textContent = updated.titulo || "";

    if (tdStatus) {
        tdStatus.innerHTML = gerarBadgeStatus(updated.status);
    }

    if (tdPrio) {
        tdPrio.innerHTML = gerarBadgePrioridade(updated.prioridade);
    }
}

function gerarBadgeStatus(status) {
    const valor = normalizarStatusParaBackend(status);

    if (valor === "ABERTO") {
        return `<span class="table-status-badge table-status-aberto">Aberto</span>`;
    }

    if (valor === "EM_ANDAMENTO") {
        return `<span class="table-status-badge table-status-andamento">Em Andamento</span>`;
    }

    if (valor === "PENDENTE") {
        return `<span class="table-status-badge table-status-pendente">Pendente</span>`;
    }

    if (valor === "FECHADO" || valor === "RESOLVIDO") {
        return `<span class="table-status-badge table-status-fechado">Fechado</span>`;
    }

    return `<span class="table-status-badge table-status-default">${escapeHtml(status || "")}</span>`;
}

function gerarBadgePrioridade(prioridade) {
    const valor = normalizarPrioridadeParaBackend(prioridade);

    if (valor === "BAIXA") {
        return `<span class="table-prio-badge table-prio-baixa">Baixa</span>`;
    }

    if (valor === "MEDIA") {
        return `<span class="table-prio-badge table-prio-media">Média</span>`;
    }

    if (valor === "ALTA") {
        return `<span class="table-prio-badge table-prio-alta">Alta</span>`;
    }

    return `<span class="table-prio-badge table-prio-default">${escapeHtml(prioridade || "")}</span>`;
}

function normalizarStatusParaBackend(status) {
    const s = String(status || "").trim().toUpperCase();

    if (s === "ABERTO") return "ABERTO";
    if (s === "EM ANDAMENTO" || s === "EM_ANDAMENTO") return "EM_ANDAMENTO";
    if (s === "PENDENTE") return "PENDENTE";
    if (s === "FECHADO" || s === "RESOLVIDO") return "FECHADO";

    return s;
}

function normalizarPrioridadeParaBackend(prioridade) {
    const p = String(prioridade || "").trim().toUpperCase();

    if (p === "BAIXA") return "BAIXA";
    if (p === "MEDIA" || p === "MÉDIA") return "MEDIA";
    if (p === "ALTA") return "ALTA";

    return p;
}

function normalizarStatusParaSelect(status) {
    return normalizarStatusParaBackend(status) || "ABERTO";
}

function normalizarPrioridadeParaSelect(prioridade) {
    return normalizarPrioridadeParaBackend(prioridade) || "BAIXA";
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

    const valor = normalizarPrioridadeParaBackend(prioridade);

    const input = document.getElementById("prioridadeInput");
    if (input) input.value = valor;

    const priorityLine = document.getElementById("priorityLine");
    if (priorityLine) {
        priorityLine.classList.remove("low", "med", "high");

        if (valor === "BAIXA") priorityLine.classList.add("low");
        if (valor === "MEDIA") priorityLine.classList.add("med");
        if (valor === "ALTA") priorityLine.classList.add("high");
    }

    const headerIconBox = document.getElementById("headerIconBox");
    if (headerIconBox) {
        headerIconBox.style.color =
            valor === "ALTA" ? "#ef4444" :
            valor === "MEDIA" ? "#f59e0b" :
            "#10b981";

        headerIconBox.style.background =
            valor === "ALTA" ? "#fef2f2" :
            valor === "MEDIA" ? "#fffbeb" :
            "#ecfdf5";
    }
}

function updateCharCount(textarea) {
    const counter = document.getElementById("charCount");
    if (counter) counter.textContent = textarea.value.length;
}

function aplicarFiltrosElite() {
    const busca = (document.getElementById("filterBusca")?.value || "").trim().toLowerCase();
    const dataFiltroISO = (document.getElementById("filterData")?.value || "").trim();

    const statusChip = document.querySelector("#statusFilterGroup .chip.active");
    const prioChip = document.querySelector("#prioFilterGroup .chip.active");

    const statusSelecionado = statusChip ? statusChip.innerText.trim() : "Tudo";
    const prioSelecionada = prioChip ? prioChip.innerText.trim() : "Tudo";

    const tbody = document.getElementById("chamadosTable");
    if (!tbody) return;

    const rows = Array.from(tbody.querySelectorAll("tr"));

    rows.forEach(row => {
        const tds = row.querySelectorAll("td");
        if (tds.length < 7) return;

        const idTxt = (tds[0]?.innerText || "").trim().toLowerCase();
        const tipoTxt = (tds[1]?.innerText || "").trim().toLowerCase();
        const setorTxt = (tds[2]?.innerText || "").trim().toLowerCase();
        const assuntoTxt = (tds[3]?.innerText || "").trim().toLowerCase();

        const statusTxt = (tds[4]?.innerText || "").trim();
        const prioridadeTxt = (tds[5]?.innerText || "").trim();

        const dataBr = (tds[6]?.innerText || "").trim();
        const dataISO = brDateToISO(dataBr);

        let mostrar = true;

        if (statusSelecionado !== "Tudo" && statusTxt !== statusSelecionado) mostrar = false;
        if (prioSelecionada !== "Tudo" && prioridadeTxt !== prioSelecionada) mostrar = false;
        if (dataFiltroISO && dataISO !== dataFiltroISO) mostrar = false;

        if (busca) {
            const textoLinha = `${idTxt} ${tipoTxt} ${setorTxt} ${assuntoTxt}`;
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

    const prioridadeInput = document.getElementById("prioridadeInput");
    if (prioridadeInput) {
        const valor = normalizarPrioridadeParaBackend(prioridadeInput.value || "BAIXA");
        const botao =
            valor === "ALTA" ? document.querySelector(".prio-card-elite.high") :
            valor === "MEDIA" ? document.querySelector(".prio-card-elite.med") :
            document.querySelector(".prio-card-elite.low");

        if (botao) setElitePriority(botao, valor);
    }
});

function aplicarBuscaTopo(busca) {
    const tbody = document.getElementById("chamadosTable");
    if (!tbody) return;

    const rows = Array.from(tbody.querySelectorAll("tr"));
    rows.forEach(row => {
        const tds = row.querySelectorAll("td");
        if (tds.length < 4) return;

        const idTxt = (tds[0]?.innerText || "").trim().toLowerCase();
        const tipoTxt = (tds[1]?.innerText || "").trim().toLowerCase();
        const setorTxt = (tds[2]?.innerText || "").trim().toLowerCase();
        const assuntoTxt = (tds[3]?.innerText || "").trim().toLowerCase();

        const textoLinha = `${idTxt} ${tipoTxt} ${setorTxt} ${assuntoTxt}`;
        const mostrar = !busca || textoLinha.includes(busca);

        row.style.display = mostrar ? "" : "none";
    });
}
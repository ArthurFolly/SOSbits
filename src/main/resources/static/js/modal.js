// --- CONTROLE DE ABERTURA E FECHAMENTO ---

function abrirModal() {
    const modal = document.getElementById('modalChamado');
    if (modal) modal.classList.add('active');
}

function fecharModal() {
    const modal = document.getElementById('modalChamado');
    if (modal) {
        modal.classList.remove('active');

        // RESET COMPLETO: Limpa campos e esconde o "Outros"
        const form = modal.querySelector('form');
        if (form) {
            form.reset();

            // Garante que o campo "Outros" suma ao fechar ou descartar
            const otherField = document.getElementById('otherField');
            if (otherField) otherField.classList.add('hidden-field');

            // Reseta visualmente para o padrão (Baixa/Verde)
            resetaPrioridadeVisual();
        }
    }
}

function abrirModalFiltros() {
    const modalFiltros = document.getElementById('modalFiltros');
    if (modalFiltros) modalFiltros.classList.add('active');
}

function fecharModalFiltros() {
    const modalFiltros = document.getElementById('modalFiltros');
    if (modalFiltros) modalFiltros.classList.remove('active');
}

// --- LÓGICA DO FORMULÁRIO DE CHAMADO ---

function resetaPrioridadeVisual() {
    const line = document.getElementById('priorityLine');
    const iconBox = document.getElementById('headerIconBox');
    const charCount = document.getElementById('charCount');

    if (line) line.style.backgroundColor = '#10b981';
    if (iconBox) iconBox.style.color = '#10b981';
    if (charCount) charCount.innerText = '0';

    document.querySelectorAll('.prio-card-elite').forEach(c => c.classList.remove('active'));
    const lowPrio = document.querySelector('.prio-card-elite.low');
    if (lowPrio) lowPrio.classList.add('active');

    // (importante) garante valor no hidden input
    const inputPrio = document.getElementById('prioridadeInput');
    if (inputPrio && !inputPrio.value) inputPrio.value = 'Baixa';
}

function setElitePriority(btn, value) {
    document.querySelectorAll('.prio-card-elite').forEach(c => c.classList.remove('active'));
    if (btn) btn.classList.add('active');

    const inputPrio = document.getElementById('prioridadeInput');
    if (inputPrio) inputPrio.value = value;

    const line = document.getElementById('priorityLine');
    const iconBox = document.getElementById('headerIconBox');
    const colors = { 'Baixa': '#10b981', 'Média': '#f59e0b', 'Alta': '#ef4444' };

    if (line) line.style.backgroundColor = colors[value];
    if (iconBox) iconBox.style.color = colors[value];
}

/**
 * ✅ CORRIGIDO:
 * Evita crash quando #otherField não existe (erro classList de null)
 */
function checkOtherOption(select) {
    const otherField = document.getElementById('otherField');
    const inputOther = document.getElementById('inputOther');

    if (!otherField) {
        console.warn('Não achei #otherField no HTML. Ignorando checkOtherOption().');
        return;
    }

    if (select && select.value === 'Outros') {
        otherField.classList.remove('hidden-field');
        if (inputOther) inputOther.required = true;
    } else {
        otherField.classList.add('hidden-field');
        if (inputOther) {
            inputOther.required = false;
            inputOther.value = "";
        }
    }
}

function updateCharCount(textarea) {
    const count = document.getElementById('charCount');
    if (count && textarea) count.innerText = textarea.value.length;
}

function handleFileSelection() {
    const input = document.getElementById('fileInput');
    const label = document.getElementById('fileNameText');
    if (!input || !label) return;

    if (input.files && input.files.length > 0) {
        label.innerHTML = `Selecionado: <strong>${input.files[0].name}</strong>`;
    }
}

// --- LÓGICA DE FILTROS (CHIPS) ---

document.querySelectorAll('.chip-group').forEach(group => {
    group.addEventListener('click', e => {
        if (e.target && e.target.classList && e.target.classList.contains('chip')) {
            const siblings = group.querySelectorAll('.chip');
            siblings.forEach(c => c.classList.remove('active'));
            e.target.classList.add('active');
        }
    });
});

function limparFiltros() {
    document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
    document.querySelectorAll('.chip').forEach(c => {
        if (c.innerText === 'Tudo') c.classList.add('active');
    });
}

// Fechar ao clicar na parte escura (Overlay)
window.onclick = function (event) {
    const modalChamado = document.getElementById('modalChamado');
    const modalFiltros = document.getElementById('modalFiltros');

    if (event.target === modalChamado) fecharModal();
    if (event.target === modalFiltros) fecharModalFiltros();
};

// ==================================================
// ✅ A PARTIR DAQUI: ENVIAR E LISTAR
// ==================================================

// Ajuste esses IDs se no seu HTML estiver diferente:
const TBODY_ID = 'chamadosTbody';          // <tbody id="chamadosTbody">
const FORM_SELECTOR = '#modalChamado form'; // ou '#formChamado'

// (Opcional) se você tiver um hidden para editar:
const ID_FIELD = 'chamadoId'; // <input type="hidden" id="chamadoId">

document.addEventListener('DOMContentLoaded', () => {
    // 1) carrega lista ao abrir a página
    carregarChamados();

    // 2) intercepta submit do form do modal (envia via AJAX)
    const form = document.querySelector(FORM_SELECTOR);
    if (!form) {
        console.warn(`Form não encontrado pelo seletor: ${FORM_SELECTOR}`);
        return;
    }

    form.addEventListener('submit', enviarChamado);
});

async function enviarChamado(e) {
    e.preventDefault();

    const id = document.getElementById(ID_FIELD)?.value?.trim() || '';

    const selectOcorrencia = document.getElementById('selectOcorrencia');
    const tipo = selectOcorrencia?.value || '';

    let assunto = '';

    const inputOther = document.getElementById('inputOther');

    // Se seu HTML tiver um input próprio para assunto, ele tem prioridade:
    const assuntoInput =
        document.querySelector('#modalChamado input[name="assunto"]') ||
        document.querySelector('#modalChamado #assunto');

    if (assuntoInput && assuntoInput.value.trim() !== '') {
        assunto = assuntoInput.value.trim();
    } else if (selectOcorrencia && selectOcorrencia.value === 'Outros' && inputOther) {
        assunto = inputOther.value.trim();
    } else {
        // fallback: usa o tipo selecionado como assunto
        assunto = tipo;
    }

    const descricao =
        document.querySelector('#modalChamado textarea[name="descricao"]')?.value?.trim() ||
        document.querySelector('#modalChamado textarea')?.value?.trim() ||
        '';

    const prioridade = document.getElementById('prioridadeInput')?.value || 'Baixa';

    // Validações mínimas
    if (!tipo) return alert('Selecione o tipo do chamado.');
    if (!assunto) return alert('Informe o assunto do chamado.');
    if (!descricao) return alert('Descreva o problema.');

    /**
     * ✅ CORRIGIDO:
     * Para evitar 500 por incompatibilidade de nome no backend,
     * enviamos "assunto" e também "titulo" como compatibilidade.
     */
    const payload = {
        tipo,
        assunto,
        titulo: assunto, // compatibilidade (se o backend usar "titulo")
        descricao,
        prioridade
    };

    try {
        const url = id ? `/chamados/${id}` : `/chamados`;
        const method = id ? 'PUT' : 'POST';

        const resp = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            console.error('Erro backend:', resp.status, txt);
            return alert('Erro ao salvar chamado. Veja o console (F12).');
        }

        fecharModal();
        await carregarChamados();

    } catch (err) {
        console.error('Falha de rede:', err);
        alert('Falha de conexão ao salvar. Veja o console (F12).');
    }
}

async function carregarChamados() {
    const tbody = document.getElementById(TBODY_ID);
    if (!tbody) {
        console.warn(`Não achei <tbody id="${TBODY_ID}">. Ajuste TBODY_ID no JS ou crie no HTML.`);
        return;
    }

    try {
        const resp = await fetch('/chamados/api', {
            headers: { 'Accept': 'application/json' }
        });

        if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            console.error('Erro ao listar:', resp.status, txt);
            return;
        }

        const chamados = await resp.json();

        tbody.innerHTML = chamados.map(c => {
            const assuntoOuTitulo = (c.assunto ?? c.titulo ?? '');

            return `
            <tr>
                <td>${c.id ?? ''}</td>
                <td>${escapeHtml(c.tipo ?? '')}</td>
                <td>${escapeHtml(assuntoOuTitulo)}</td>
                <td>${escapeHtml(c.prioridade ?? '')}</td>
                <td>${escapeHtml(c.status ?? '')}</td>
                <td>
                    <button type="button" onclick="editarChamado(${c.id})">Editar</button>
                    <button type="button" onclick="excluirChamado(${c.id})">Excluir</button>
                </td>
            </tr>
        `;
        }).join('');

    } catch (err) {
        console.error('Falha ao listar:', err);
    }
}

// Preenche modal para editar
async function editarChamado(id) {
    try {
        const resp = await fetch(`/chamados/${id}`, {
            headers: { 'Accept': 'application/json' }
        });
        if (!resp.ok) return alert('Erro ao carregar chamado.');

        const c = await resp.json();
        const assuntoOuTitulo = (c.assunto ?? c.titulo ?? '');

        // seta ID
        const idEl = document.getElementById(ID_FIELD);
        if (idEl) idEl.value = c.id;

        // tipo
        const select = document.getElementById('selectOcorrencia');
        if (select) select.value = c.tipo ?? '';

        // assunto (se tiver input)
        const assuntoInput =
            document.querySelector('#modalChamado input[name="assunto"]') ||
            document.querySelector('#modalChamado #assunto');
        if (assuntoInput) assuntoInput.value = assuntoOuTitulo;

        // descrição
        const textarea =
            document.querySelector('#modalChamado textarea[name="descricao"]') ||
            document.querySelector('#modalChamado textarea');
        if (textarea) textarea.value = c.descricao ?? '';

        // prioridade
        const prio = document.getElementById('prioridadeInput');
        if (prio) prio.value = c.prioridade ?? 'Baixa';

        abrirModal();

    } catch (err) {
        console.error('Falha ao editar:', err);
    }
}

async function excluirChamado(id) {
    if (!confirm('Deseja excluir este chamado?')) return;

    try {
        const resp = await fetch(`/chamados/${id}`, { method: 'DELETE' });
        if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            console.error('Erro ao excluir:', resp.status, txt);
            return alert('Erro ao excluir chamado.');
        }

        await carregarChamados();
    } catch (err) {
        console.error('Falha ao excluir:', err);
    }
}

// Segurança básica ao renderizar texto na tabela
function escapeHtml(str) {
    return String(str)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

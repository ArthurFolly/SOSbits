document.addEventListener('DOMContentLoaded', () => {
    // ✅ IMPORTANTE: este JS só deve rodar na página de chamados
    // No chamados.html coloque: <body id="page-chamados">
    if (!document.getElementById('page-chamados')) return;

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

    // Fechar ao clicar na parte escura (Overlay)
    window.addEventListener('click', (event) => {
        const modalChamado = document.getElementById('modalChamado');
        const modalFiltros = document.getElementById('modalFiltros');

        if (event.target === modalChamado) fecharModal();
        if (event.target === modalFiltros) fecharModalFiltros();
    });

    // Ajusta contador se estiver editando (textarea já preenchida pelo Thymeleaf)
    const textarea = document.querySelector('#modalChamado textarea[name="descricao"]');
    if (textarea) updateCharCount(textarea);
});


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

    // garante valor no hidden input
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
 * Evita crash quando #otherField não existe
 */
function checkOtherOption(select) {
    const otherField = document.getElementById('otherField');
    const inputOther = document.getElementById('inputOther');

    if (!otherField) return;

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

function limparFiltros() {
    document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
    document.querySelectorAll('.chip').forEach(c => {
        if (c.innerText.trim() === 'Tudo') c.classList.add('active');
    });
}
function abrirModalGenerico(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add("active");
}

function fecharModalGenerico(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove("active");
}



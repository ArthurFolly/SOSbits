// --- MODAL NOVO CHAMADO ---
window.abrirModal = function() {
    console.log("Abrindo modal de novo chamado...");
    const modal = document.getElementById('modalChamado');
    if (modal) {
        modal.style.display = 'flex';
    }
};

window.fecharModal = function() {
    console.log("Fechando modal de novo chamado...");
    const modal = document.getElementById('modalChamado');
    if (modal) {
        modal.style.display = 'none';
    }
};

// --- MODAL DE FILTROS ---
window.abrirModalFiltros = function() {
    console.log("Abrindo modal de filtros...");
    const modal = document.getElementById('modalFiltros');
    if (modal) {
        modal.style.display = 'flex';
    }
};

window.fecharModalFiltros = function() {
    console.log("Fechando modal de filtros...");
    const modal = document.getElementById('modalFiltros');
    if (modal) {
        modal.style.display = 'none';
    }
};

// --- LÓGICA DOS CHIPS (FILTROS) ---
// Esta parte faz com que, ao clicar em um chip, ele fique ativo e os outros não
document.addEventListener('DOMContentLoaded', () => {
    const chips = document.querySelectorAll('.chip');

    chips.forEach(chip => {
        chip.addEventListener('click', function() {
            // Seleciona apenas os chips do mesmo grupo (Status ou Prioridade)
            const grupo = this.parentElement;
            grupo.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));

            // Adiciona a classe active no chip clicado
            this.classList.add('active');
        });
    });
});

// --- FECHAR AO CLICAR FORA ---
window.onclick = function(event) {
    const modalChamado = document.getElementById('modalChamado');
    const modalFiltros = document.getElementById('modalFiltros');

    if (event.target === modalChamado) {
        fecharModal();
    }
    if (event.target === modalFiltros) {
        fecharModalFiltros();
    }
};

// --- FUNÇÃO LIMPAR FILTROS ---
window.limparFiltros = function() {
    // Volta todos os grupos para o primeiro chip (geralmente "Tudo" ou "Todas")
    document.querySelectorAll('.chip-group').forEach(grupo => {
        grupo.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
        grupo.firstElementChild.classList.add('active');
    });
    console.log("Filtros resetados");
};
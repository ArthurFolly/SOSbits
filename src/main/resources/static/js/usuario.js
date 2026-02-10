

function abrirModalFiltroUsuarios() {
    const modal = document.getElementById('modalFiltroUsuarios');
    if (modal) modal.classList.add('active');
}

function fecharModalFiltroUsuarios() {
    const modal = document.getElementById('modalFiltroUsuarios');
    if (modal) modal.classList.remove('active');
}


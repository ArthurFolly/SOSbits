

function abrirModalGenerico(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('active');
}

function fecharModalGenerico(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('active');
}

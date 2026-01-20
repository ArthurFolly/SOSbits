function abrirModal(event) {
    if (event) event.preventDefault();
    document.getElementById('modalChamado').style.display = 'flex';
}

function fecharModal() {
    document.getElementById('modalChamado').style.display = 'none';
}

// Fechar se clicar fora da caixa branca
window.onclick = function(event) {
    const modal = document.getElementById('modalChamado');
    if (event.target == modal) {
        fecharModal();
    }
}
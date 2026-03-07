/* =========================================================
   MODAIS GENÉRICOS
   =========================================================
   Funções reutilizáveis para abrir e fechar modais do sistema
   ========================================================= */

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


/* =========================================================
   BUSCA NA TABELA DE AVALIAÇÕES
   =========================================================
   Filtra dinamicamente os resultados digitados no campo
   de busca da página de avaliações
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {

    const inputBusca = document.getElementById("avaliacoesSearchInput");

    const tabelaBody = document.getElementById("avaliacoesTableBody");

    if (!inputBusca || !tabelaBody) return;

    inputBusca.addEventListener("input", () => {

        const termo = inputBusca.value.toLowerCase();

        const linhas = tabelaBody.querySelectorAll("tr");

        linhas.forEach(linha => {

            /* ignora linha "nenhum resultado" */

            if (linha.querySelector("td[colspan]")) return;

            const textoLinha = linha.innerText.toLowerCase();

            linha.style.display =
                textoLinha.includes(termo) ? "" : "none";

        });

    });

});


/* =========================================================
   FECHAR MODAIS COM ESC
   ========================================================= */

document.addEventListener("keydown", (e) => {

    if (e.key !== "Escape") return;

    document.querySelectorAll(".modal-overlay").forEach(modal => {

        if (modal.style.display === "flex") {

            modal.style.display = "none";

        }

    });

    document.body.classList.remove("modal-open");

});


/* =========================================================
   MODAL DETALHE DA AVALIAÇÃO
   =========================================================
   Preenche os dados da avaliação ao clicar no botão "olho"
   ========================================================= */

function abrirModalDetalheAvaliacao(botao) {

    const linha = botao.closest("tr");

    if (!linha) return;

    const colunas = linha.querySelectorAll("td");

    if (colunas.length < 4) return;


    /* =============================
       COLETA DADOS DA TABELA
       ============================= */

    const id = colunas[0].innerText.trim();

    const chamado = colunas[1].innerText.trim();

    const avaliador = colunas[2].innerText.trim();

    const nota = parseInt(colunas[3].innerText.trim());


    /* comentário vem do atributo data */
    const comentario = botao.getAttribute("data-comentario") || "—";


    /* =============================
       PREENCHE MODAL
       ============================= */

    setText("detAvaliacaoId", id);

    setText("detChamado", chamado);

    setText("detAvaliador", avaliador);

    setText("detComentario", comentario);


    /* renderiza estrelas */

    renderStars(nota);


    abrirModalGenerico("modalDetalheAvaliacao");

}


/* =========================================================
   FUNÇÃO AUXILIAR PARA DEFINIR TEXTO
   ========================================================= */

function setText(idElemento, valor) {

    const elemento = document.getElementById(idElemento);

    if (!elemento) return;

    elemento.textContent = valor;

}


/* =========================================================
   RENDERIZA ESTRELAS DA NOTA
   ========================================================= */

function renderStars(nota) {

    const container = document.getElementById("detNota");

    if (!container) return;

    container.innerHTML = "";


    /* cria estrelas */

    for (let i = 1; i <= 5; i++) {

        const estrela = document.createElement("i");

        estrela.className =
            i <= nota
                ? "fas fa-star estrela-on"
                : "far fa-star estrela-off";

        container.appendChild(estrela);

    }


    /* adiciona texto da nota */

    const textoNota = document.createElement("span");

    textoNota.className = "nota-texto";

    textoNota.innerText = ` (${nota})`;

    container.appendChild(textoNota);

}
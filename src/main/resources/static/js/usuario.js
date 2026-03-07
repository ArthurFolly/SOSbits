(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        ativarBuscaUsuarios();
        ativarCliqueForaParaFecharModais();
        ativarTeclaESCParaFecharModais();
        abrirModalEdicaoSeNecessario(); // ✅ FALTAVA ISSO
    });

    function ativarBuscaUsuarios() {
        const input = document.getElementById("usuariosSearchInput");
        const tbody = document.getElementById("usuariosTableBody");

        if (!input || !tbody) return;

        input.addEventListener("input", function () {
            const termo = (input.value || "").trim().toLowerCase();
            const linhas = tbody.querySelectorAll("tr");

            linhas.forEach((tr) => {
                const textoLinha = (tr.innerText || "").toLowerCase();
                tr.style.display = !termo || textoLinha.includes(termo) ? "" : "none";
            });
        });
    }

    window.abrirModalGenerico = function (modalId) {
        const overlay = document.getElementById(modalId);
        if (!overlay) return;

        overlay.classList.add("active");
        overlay.style.display = "flex";
        document.body.style.overflow = "hidden";
    };

    window.fecharModalGenerico = function (modalId) {
        const overlay = document.getElementById(modalId);
        if (!overlay) return;

        overlay.classList.remove("active");
        overlay.style.display = "none";

        const aindaTemModalAberto = document.querySelector(".modal-overlay.active");
        if (!aindaTemModalAberto) {
            document.body.style.overflow = "";
        }
    };

    window.abrirModalUsuariosExcluidos = function () {
        abrirModalGenerico("modalUsuariosExcluidos");
    };

    window.fecharModalUsuariosExcluidos = function () {
        fecharModalGenerico("modalUsuariosExcluidos");
    };

    function ativarCliqueForaParaFecharModais() {
        document.addEventListener("click", function (event) {
            const overlay = event.target;

            if (overlay && overlay.classList && overlay.classList.contains("modal-overlay")) {
                if (overlay.classList.contains("active")) {
                    fecharModalGenerico(overlay.id);
                }
            }
        });
    }
    function ativarTeclaESCParaFecharModais() {
        document.addEventListener("keydown", function (e) {
            if (e.key !== "Escape") return;

            document.querySelectorAll(".modal-overlay.active").forEach((overlay) => {
                fecharModalGenerico(overlay.id);
            });
        });
    }
    function abrirModalEdicaoSeNecessario() {
        const flag = document.getElementById("modoEdicaoFlag");
        if (!flag) return;

        const isEdicao = String(flag.value || "").toLowerCase() === "true";
        if (isEdicao) {
            abrirModalGenerico("modalNovoUsuario");
        }
    }

/* =========================================
CONSULTA CEP VIA VIACEP
========================================= */

const cepInput = document.getElementById("cep");
const btnBuscarCep = document.getElementById("btnBuscarCep");

if(btnBuscarCep){

btnBuscarCep.addEventListener("click", buscarCep);

}

async function buscarCep(){

let cep = cepInput.value.replace(/\D/g,'');

if(cep.length !== 8){

alert("CEP inválido");
return;

}

try{

const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);

const data = await response.json();

if(data.erro){

alert("CEP não encontrado");
return;

}

/* PREENCHE CAMPOS */

document.querySelector('[name="logradouro"]').value = data.logradouro || "";
document.querySelector('[name="bairro"]').value = data.bairro || "";
document.querySelector('[name="cidade"]').value = data.localidade || "";
document.querySelector('[name="estado"]').value = data.uf || "";
document.querySelector('[name="complemento"]').value = data.complemento || "";

/* FOCO NO NUMERO */

document.querySelector('[name="numero"]').focus();

}catch(error){

alert("Erro ao consultar CEP");

}

}

})();
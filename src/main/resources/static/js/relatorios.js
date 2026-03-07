document.addEventListener("DOMContentLoaded", function () {
    inicializarGraficoAvaliacoes();
});

function inicializarGraficoAvaliacoes() {
    const canvas = document.getElementById("graficoAvaliacoes");

    if (!canvas) {
        return;
    }

    // =========================================================
    // CAPTURA DOS DADOS VINDOS DO HTML
    // Espera encontrar no canvas os atributos:
    // data-qtd1, data-qtd2, data-qtd3, data-qtd4, data-qtd5
    // =========================================================
    const qtd1 = Number(canvas.dataset.qtd1 || 0);
    const qtd2 = Number(canvas.dataset.qtd2 || 0);
    const qtd3 = Number(canvas.dataset.qtd3 || 0);
    const qtd4 = Number(canvas.dataset.qtd4 || 0);
    const qtd5 = Number(canvas.dataset.qtd5 || 0);

    const dados = [qtd1, qtd2, qtd3, qtd4, qtd5];
    const total = dados.reduce((acumulador, valor) => acumulador + valor, 0);

    // =========================================================
    // FUNÇÃO PARA CALCULAR PERCENTUAL
    // =========================================================
    function calcularPercentual(valor) {
        if (total === 0) {
            return "0,0%";
        }

        return ((valor / total) * 100).toFixed(1).replace(".", ",") + "%";
    }

    // =========================================================
    // RÓTULOS PROFISSIONAIS DA LEGENDA
    // =========================================================
    const labels = [
        `1 estrela  •  ${qtd1}  •  ${calcularPercentual(qtd1)}`,
        `2 estrelas •  ${qtd2}  •  ${calcularPercentual(qtd2)}`,
        `3 estrelas •  ${qtd3}  •  ${calcularPercentual(qtd3)}`,
        `4 estrelas •  ${qtd4}  •  ${calcularPercentual(qtd4)}`,
        `5 estrelas •  ${qtd5}  •  ${calcularPercentual(qtd5)}`
    ];

    const cores = [
        "#ef4444", // vermelho
        "#f97316", // laranja
        "#facc15", // amarelo
        "#4ade80", // verde claro
        "#22c55e"  // verde
    ];

    // =========================================================
    // DESTRÓI O GRÁFICO ANTERIOR, SE EXISTIR
    // Evita duplicação quando a tela recarrega dinamicamente
    // =========================================================
    if (window.graficoAvaliacoesInstancia) {
        window.graficoAvaliacoesInstancia.destroy();
    }

    // =========================================================
    // CRIAÇÃO DO GRÁFICO
    // =========================================================
    window.graficoAvaliacoesInstancia = new Chart(canvas, {
        type: "pie",

        data: {
            labels: labels,
            datasets: [
                {
                    data: dados,
                    backgroundColor: cores,
                    borderColor: "#ffffff",
                    borderWidth: 2,
                    hoverOffset: 12
                }
            ]
        },

        options: {
            responsive: true,
            maintainAspectRatio: false,

            layout: {
                padding: {
                    top: 10,
                    right: 20,
                    bottom: 10,
                    left: 20
                }
            },

            plugins: {
                title: {
                    display: false
                },

                legend: {
                    display: true,
                    position: "left",
                    align: "center",

                    labels: {
                        color: "#334155",
                        boxWidth: 18,
                        boxHeight: 18,
                        padding: 18,
                        usePointStyle: false,

                        font: {
                            size: 13,
                            weight: "600",
                            family: "Arial"
                        }
                    }
                },

                tooltip: {
                    backgroundColor: "#0f172a",
                    titleColor: "#ffffff",
                    bodyColor: "#e2e8f0",
                    borderColor: "#334155",
                    borderWidth: 1,
                    padding: 12,
                    displayColors: true,

                    callbacks: {
                        label: function (context) {
                            const valor = context.raw || 0;
                            const percentual = calcularPercentual(valor);

                            return ` ${valor} avaliação(ões) • ${percentual}`;
                        }
                    }
                }
            }
        }
    });
}
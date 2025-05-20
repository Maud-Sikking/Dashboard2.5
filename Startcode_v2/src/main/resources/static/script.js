// URL van Hasura endpoint
const HASURA_URL = "https://jouw-hasura-endpoint/v1/graphql";

const createCenterTextPlugin = (text) => ({
    id: 'centerText',
    beforeDraw(chart) {
        const { width, height, ctx } = chart;
        ctx.restore();

        const fontSize = (height / 150).toFixed(2);
        ctx.font = `${fontSize}em sans-serif`;
        ctx.textBaseline = "middle";

        const textX = Math.round((width - ctx.measureText(text).width) / 2);
        const textY = height / 2;

        ctx.fillText(text, textX, textY);
        ctx.save();
    }
});

function renderDoughnutChart(canvasId, labels, values, chartTitle) {
    const total = values.reduce((sum, val) => sum + val, 0);
    const ctx = document.getElementById(canvasId).getContext("2d");

    new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)',
                    'rgba(255, 159, 64, 0.6)'
                ],
                borderColor: 'white',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                title: {
                    display: true,
                    text: chartTitle
                }
            }
        },
        plugins: [createCenterTextPlugin(total.toString())]
    });
}

function loadDoughnutCharts() {
    fetch("/api/alerts-soorten-januari")
        .then(res => res.json())
        .then(data => {
            const result = data.data.soort_alert_januari_2025
                .filter(row => row.type !== "FRAME_OFFLINE");
            const labels = result.map(row => row.type);
            const values = result.map(row => row.alert_count);
            renderDoughnutChart("alertDoughnutChart", labels, values, "Alerts januari");
        });

}


// Inlogfunctie
document.getElementById('loginForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();

    // Haal gebruikersnaam en wachtwoord op uit het formulier
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        // Simuleer authenticatie (gebruik Hasura JWT of een externe API voor beveiliging)
        const response = await fetch(`${HASURA_URL}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                query: `
                    query loginUser {
                        login(username: "${username}", password: "${password}") {
                            token
                        }
                    }
                `
            })
        });

        const data = await response.json();

        if (response.ok && data.data?.login?.token) {
            // Sla token op in Local Storage
            localStorage.setItem("authToken", data.data.login.token);

            // Redirect naar een beveiligde pagina
            window.location.href = "./dashboard.html";
        } else {
            document.getElementById('error').style.display = "block";
        }
    } catch (error) {
        console.error("Fout bij inloggen:", error);
        document.getElementById('error').style.display = "block";
    }
});

// Uitlogfunctie
document.getElementById('logoutButton')?.addEventListener('click', function () {
    // Verwijder token uit Local Storage
    localStorage.removeItem("authToken");

    // Redirect naar de inlogpagina
    window.location.href = "./index.html";
});

// Beveiligde pagina validatie
function checkAuth() {
    const token = localStorage.getItem("authToken");
    if (!token) {
        // Geen token aanwezig, terug naar inlogpagina
        window.location.href = "./login.html";
    }
}

// Voer checkAuth() uit op beveiligde pagina's
if (window.location.pathname.includes("dashboard.html")) {
    checkAuth();
}

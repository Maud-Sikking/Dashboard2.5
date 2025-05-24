// URL van Hasura endpoint
const HASURA_URL = "https://jouw-hasura-endpoint/v1/graphql";

// voor titel van charts
const monthNames = [
    "januari", "februari", "maart", "april", "mei", "juni",
    "juli", "augustus", "september", "oktober", "november", "december"
];

//
const doughnutCharts = {};
const deviceAlertCharts = {};



// Vul jaar dropdown van 2013 t/m huidig jaar
function vulJaarDropdown() {
    const jaarSelect = document.getElementById("yearSelect");
    const huidigJaar = new Date().getFullYear();
    for (let jaar = huidigJaar; jaar >= 2013; jaar--) {
        const option = document.createElement("option");
        option.value = jaar;
        option.textContent = jaar;
        jaarSelect.appendChild(option);
    }
    // Selecteer huidig jaar
    jaarSelect.value = huidigJaar;
}

// zet maand dropdown op default = huidige maand
function zetMaandDropdown() {
    const maandSelect = document.getElementById("monthSelect");
    const huidigMaand = (new Date().getMonth() + 1).toString().padStart(2, "0"); // 1-12 => "01"-"12"
    maandSelect.value = huidigMaand;
}

// zorgt dat default charts kunnen runnen
window.addEventListener("DOMContentLoaded", () => {
    vulJaarDropdown();
    zetMaandDropdown();

    const jaar = document.getElementById("yearSelect").value;
    const maand = document.getElementById("monthSelect").value;
    const monthParam = `${jaar}-${maand}`;

    updateDoughnutChart("alertDoughnutChart", monthParam);
    updateAantalActieveDevices("activeDevicesTitle", "activeDevices", monthParam);
    updateDeviceAlertBarChart("deviceAlertBarChart", monthParam);
});

// Wanneer op 'Toon' geklikt wordt:
document.getElementById("filterBtn").addEventListener("click", () => {
    const maand = document.getElementById("monthSelect").value;
    const jaar = document.getElementById("yearSelect").value;

    // Maak "YYYY-MM" string
    const monthParam = `${jaar}-${maand}`;

    // Roep je alle functies aan die charts laten zien en updaten
    updateDoughnutChart("alertDoughnutChart", monthParam);
    updateAantalActieveDevices("activeDevicesTitle", "activeDevices", monthParam);
    updateDeviceAlertBarChart("deviceAlertBarChart", monthParam)
});

// voor titels
function updateDateTitle(selectedYearMonth) {
    if (selectedYearMonth) {
        // selectedYearMonth = "YYYY-MM"
        const [jaar, maand] = selectedYearMonth.split("-");
        const maandNaam = monthNames[parseInt(maand, 10) - 1];
        return `${maandNaam} ${jaar}`;
    }
}

function updateDoughnutChart(canvasId, selectedYearMonth) {
    fetch(`/api/alerts?month=${selectedYearMonth}`)
        .then(res => {
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            return res.json();
        })
        .then(data => {

            const canvas= document.getElementById(canvasId);
            const melding = document.getElementById("geenDataMeldingDonut")
            const alerts = data.data.alerts || [];

            // Update de titel boven de chart
            document.getElementById("chartTitle").textContent = `Alerts overzicht voor ` + updateDateTitle(selectedYearMonth);


            // verwijdert eerst eventuele donutcharts
            if (doughnutCharts[canvasId]) {
                doughnutCharts[canvasId].destroy();
            }

            // Als geen data beschikbaar is
            if (!alerts || alerts.length === 0) {
                canvas.style.display = "none";
                melding.style.display = "block";
                return;
            }

            // Verberg "geen data" en toon canvas
            canvas.style.display = "block";
            melding.style.display = "none";

            const counts = {};
            alerts.forEach(row => {
                if (row.type !== "FRAME_OFFLINE") {
                    counts[row.type] = (counts[row.type] || 0) + 1;
                }
            });

            const netteLabels = {
                BATTERY: "Batterij Genus",
                DAY_RHYTHM_DETECTION: "Ongebruikelijke dagstart",
                EV04_LOW_BATTERY: "Batterij 4G alarmknop",
                FRAME_SHUTDOWN: "Genus Offline",
                MESSAGE_MEDICINE_ANSWER_NO: "Geen reactie op medicijnbericht",
                TEMPERATURE_TOO_HIGH: "Te hoge temperatuur",
                TEMPERATURE_TOO_LOW: "Te lage temperatuur",
                EV04_SHUTDOWN: "4G alarmknop offline",
                EV04_FALL: "Val alarm",
                MESSAGE_NO_RESPONSE: "Niet op tijd reactie op bericht",
                MESSAGE_MEDICINE_NO_RESPONSE: "Medicijnen niet ingenomen"
            };

            const kleurenPerType = {
                BATTERY: 'rgba(255, 99, 132, 0.6)',                    // roodachtig
                DAY_RHYTHM_DETECTION: 'rgba(54, 162, 235, 0.6)',       // blauw
                EV04_LOW_BATTERY: 'rgba(255, 206, 86, 0.6)',           // geel
                FRAME_SHUTDOWN: 'rgba(75, 192, 192, 0.6)',             // groenblauw
                MESSAGE_MEDICINE_ANSWER_NO: 'rgba(153, 102, 255, 0.6)',// paars
                TEMPERATURE_TOO_HIGH: 'rgba(255, 159, 64, 0.6)',       // oranje
                TEMPERATURE_TOO_LOW: 'rgba(224, 31, 31, 0.75)',        // donkerrood
                EV04_SHUTDOWN: 'rgba(91, 225, 255, 0.69)',             // lichtblauw
                EV04_FALL: 'rgba(255, 102, 204, 0.6)',                 // roze
                MESSAGE_NO_RESPONSE: 'rgba(102, 80, 255, 0.6)',       // blauwpaars
                MESSAGE_MEDICINE_NO_RESPONSE: 'rgba(0, 191, 165, 0.6)' // teal
            };

            const types = Object.keys(counts);
            const labels = types.map(type => netteLabels[type] || type);
            const values = types.map(type => counts[type]);
            const backgroundColors = types.map(type => kleurenPerType[type] || 'rgba(200,200,200,0.6)');

            const ctx = canvas.getContext("2d");
            const total = values.reduce((a, b) => a + b, 0);

            // totaal alerts in het midden te tonen van chart
            const centerTextPlugin = {
                id: 'centerText',
                beforeDraw(chart) {
                    const { width } = chart;
                    const { height } = chart;
                    const ctx = chart.ctx;
                    ctx.restore();
                    const fontSize = (height / 150).toFixed(2);
                    ctx.font = `${fontSize}em sans-serif`;
                    ctx.textBaseline = "middle";

                    const text = `${total}`;
                    const textX = Math.round((width - ctx.measureText(text).width) / 2);
                    const textY = height / 2;

                    ctx.fillStyle = "#333";
                    ctx.fillText(text, textX, textY);
                    ctx.save();
                }
            };


            doughnutCharts[canvasId] = new Chart(ctx, {
                type: "doughnut",
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: backgroundColors,
                        borderColor: 'white',
                        borderWidth: 1
                    }]
                },
                options: {
                    plugins: {
                        legend: {
                            position: 'bottom',
                            align: 'start',
                            labels: {
                                boxWidth: 12,
                                padding: 10,
                                usePointStyle: true
                            }
                        },
                    }
                },
                plugins: [centerTextPlugin]
            });
        })
        .catch(err => {
            console.error('Fout bij laden of verwerken data:', err);
        });
}
function updateAantalActieveDevices(titleId, aantalId, selectedYearMonth) {
    fetch(`/api/devices?month=${selectedYearMonth}`)
        .then(res => res.json())
        .then(data => {
            const aantalDevices = data.data.devices.length;
            const titel = document.getElementById(titleId)
            const aantalID = document.getElementById(aantalId)
            const melding = document.getElementById("geenDataMelding")

            // Update de titel boven de chart
            titel.textContent = `Aantal actieve devices voor ` + updateDateTitle(selectedYearMonth);
            aantalID.textContent = aantalDevices;

            // Als geen data beschikbaar is
            if (!aantalDevices || aantalDevices.length === 0) {
                aantalID.style.display = "none";
                melding.style.display = "block";
                return;
            }

            // Verberg "geen data" en toon aantal
            aantalID.style.display = "block";
            melding.style.display = "none";


        })
        .catch(err => {
            console.error("Fout bij ophalen actieve devices:", err);
            aantalID.textContent = "Kan data niet ophalen";
        });
}

async function updateDeviceAlertBarChart(canvasId, selectedYearMonth) {
    const deviceRes = await fetch(`/api/devices?month=${selectedYearMonth}`);
    const alertRes = await fetch(`/api/alerts?month=${selectedYearMonth}`);

    const devices = (await deviceRes.json()).data.devices;
    const alerts = (await alertRes.json()).data.alerts;

    const canvas = document.getElementById(canvasId);
    const melding = document.getElementById("geenDataMeldingBar");


    // Update de titel boven de chart
    document.getElementById("barTitle").textContent = `Aantal devices per aantal alerts voor ` + updateDateTitle(selectedYearMonth);

    // verwijdert eventuele charts
    if (deviceAlertCharts[canvasId]) {
        deviceAlertCharts[canvasId].destroy();
    }

    // Als geen data beschikbaar is
    if (!devices || devices.length === 0) {
        canvas.style.display = "none";
        melding.style.display = "block";
        return;
    }

    // Verberg "geen data" en toon canvas
    canvas.style.display = "block";
    melding.style.display = "none";


    // Tel alerts per frameUUID
    const alertCounts = {};
    alerts.forEach(alert => {
        const frameUUID = alert.frameUUID;
        alertCounts[frameUUID] = (alertCounts[frameUUID] || 0) + 1;
    });

    // Zet devices in alert-buckets
    const buckets = {
        "0": 0,
        "1–5": 0,
        "6–10": 0,
        "11–20": 0,
        "21–50": 0,
        "51–100": 0,
        "101–200": 0,
        "201–500": 0,
        "500+": 0
    };

    devices.forEach(device => {
        const count = alertCounts[device.frameDeviceIdentifier] || 0;
        if (count === 0) buckets["0"]++;
        else if (count <= 5) buckets["1–5"]++;
        else if (count <= 10) buckets["6–10"]++;
        else if (count <= 20) buckets["11–20"]++;
        else if (count <= 50) buckets["21–50"]++;
        else if (count <= 100) buckets["51–100"]++;
        else if (count <= 200) buckets["101–200"]++;
        else if (count <= 500) buckets["201–500"]++;
        else buckets["500+"]++;
    });

    // Chart.js bar chart
    const labels = Object.keys(buckets);
    const values = Object.values(buckets);

    const ctx = canvas.getContext("2d");

    deviceAlertCharts[canvasId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: "Aantal devices",
                data: values,
                backgroundColor: "rgba(54, 162, 235, 0.7)",
                borderColor: "rgba(54, 162, 235, 1)",
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 10
                    },
                    title: { display: true, text: "Aantal devices" }
                },
                x: {
                    title: { display: true, text: "Aantal alerts per device" }
                }
            }
        }
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

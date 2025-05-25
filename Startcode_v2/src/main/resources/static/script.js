// URL van Hasura endpoint
const HASURA_URL = "https://jouw-hasura-endpoint/v1/graphql";

// voor titel van charts
const monthNames = [
    "januari", "februari", "maart", "april", "mei", "juni",
    "juli", "augustus", "september", "oktober", "november", "december"
];

//
const donutCharts = {};
const deviceAlertCharts = {};
const idBarCharts = {};



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
    // Selecteer huidig jaar voor default
    jaarSelect.value = huidigJaar;
}

// zet maand dropdown op default = huidige maand
function zetMaandDropdown() {
    const maandSelect = document.getElementById("monthSelect");
    const huidigMaand = (new Date().getMonth() + 1).toString().padStart(2, "0"); // 1-12 => "01"-"12"
    maandSelect.value = huidigMaand;
}

// Maakt YYYY-MM string
function getYearMonth (){
    const jaar = document.getElementById("yearSelect").value;
    const maand = document.getElementById("monthSelect").value;
    return `${jaar}-${maand}`;
}

// zorgt dat default charts kunnen runnen
window.addEventListener("DOMContentLoaded", () => {
    const bodyClass = document.body.classList;
    vulJaarDropdown();
    zetMaandDropdown();

    if (bodyClass.contains("dash-pagina")) {
        updateDonutChart("alertDonutChart", getYearMonth ());
        updateAantalActieveDevices("activeDevicesTitle", "activeDevices", getYearMonth ());
        updateDeviceAlertBarChart("deviceAlertBarChart", getYearMonth ());


    }
    if (bodyClass.contains("zorgprof-pagina")) {
        updateIdChart("messageIdChart", getYearMonth())
        avgCommunityCall("gemCall")

    }


});

// Algemene functie om event listener veilig toe te voegen als element bestaat
function addClickListener(id, callback) {
    const el = document.getElementById(id);
    if (el) {
        el.addEventListener("click", callback);
    }
}

// Voor filterBtn op pagina algemeen dashboard (dash.html)
addClickListener("filterBtn", () => {
    updateDonutChart("alertDonutChart", getYearMonth());
    updateAantalActieveDevices("activeDevicesTitle", "activeDevices", getYearMonth());
    updateDeviceAlertBarChart("deviceAlertBarChart", getYearMonth());
});

// Voor filterBtnZorgprof op pagina zorgprofessionals (zorgprof.html)
addClickListener("filterBtnZorgprof", () => {
    updateIdChart("messageIdChart", getYearMonth());

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

// maakt/update type alert chart
function updateDonutChart(canvasId, selectedYearMonth) {
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
            if (donutCharts[canvasId]) {
                donutCharts[canvasId].destroy();
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


            // alerts worden geteld op type en frame_offline wordt overgeslagen
            const counts = {};
            alerts.forEach(row => {
                if (row.type !== "FRAME_OFFLINE") {
                    counts[row.type] = (counts[row.type] || 0) + 1;
                }
            });

            //nette tabel labels worden gemaakt
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

            //vaste kleuren worden gebruikt per type
            const kleurenPerType = {
                BATTERY: 'rgba(255, 99, 132, 0.6)',
                DAY_RHYTHM_DETECTION: 'rgba(54, 162, 235, 0.6)',
                EV04_LOW_BATTERY: 'rgba(255, 206, 86, 0.6)',
                FRAME_SHUTDOWN: 'rgba(75, 192, 192, 0.6)',
                MESSAGE_MEDICINE_ANSWER_NO: 'rgba(153, 102, 255, 0.6)',
                TEMPERATURE_TOO_HIGH: 'rgba(255, 159, 64, 0.6)',
                TEMPERATURE_TOO_LOW: 'rgba(224, 31, 31, 0.75)',
                EV04_SHUTDOWN: 'rgba(91, 225, 255, 0.69)',
                EV04_FALL: 'rgba(255, 102, 204, 0.6)',
                MESSAGE_NO_RESPONSE: 'rgba(102, 80, 255, 0.6)',
                MESSAGE_MEDICINE_NO_RESPONSE: 'rgba(0, 191, 165, 0.6)'
            };

            const types = Object.keys(counts); // typenamen uit counts
            const labels = types.map(type => netteLabels[type] || type); //typenamen krijgen nette labels
            const values = types.map(type => counts[type]); // aantal alerts per type
            const backgroundColors = types.map(type => kleurenPerType[type] || 'rgba(200,200,200,0.6)'); // types krijgen eigen kleur

            // context voor het tekenen van een 2D-chart
            const ctx = canvas.getContext("2d");

            // totaal aantal alerts
            const total = values.reduce((a, b) => a + b, 0);

            // totaal alerts in het midden tonen van chart
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

            // chart wordt gemaakt
            donutCharts[canvasId] = new Chart(ctx, {
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

//maakt/update aantal actieve devices
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

//maakt/update alert bar chart
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

    //zoeken hoeveel alerts elk device heeft. Geen? dan 0
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


    const labels = Object.keys(buckets); // X-as
    const values = Object.values(buckets); //  Y-as

    // Context voor het tekenen van een 2D-chart
    const ctx = canvas.getContext("2d");

    // chart wordt gemaakt
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

//maakt/update originId message chart
function updateIdChart(canvasId, selectedYearMonth) {
    fetch(`/api/messages?month=${selectedYearMonth}`)
        .then(response => response.json())
        .then(data => {
            const messages = data.data.messages;
            const canvas = document.getElementById(canvasId);
            const melding = document.getElementById("geenDataMeldingIdBar");


            // Update de titel boven de chart
            document.getElementById("idBarTitle").textContent = `Berichten van ` + updateDateTitle(selectedYearMonth) + ` minus een jaar`;

            // verwijdert eventuele charts
            if (idBarCharts[canvasId]) {
                idBarCharts[canvasId].destroy();
            }

            // Als geen data beschikbaar is
            if (!messages || messages.length === 0) {
                canvas.style.display = "none";
                melding.style.display = "block";
                return;
            }

            // Verberg "geen data" en toon canvas
            canvas.style.display = "block";
            melding.style.display = "none";

            // Verwerk data per maand
            const maandData = {};


            //groepering van berichten per maan en telling originId
            messages.forEach(msg => {
                const date = new Date(msg.sent);
                const maand = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`; //"2025-01"
                if (!maandData[maand]) maandData[maand] = {}; // als maand nog niet bestaat dan wordt hij aangemaakt
                const originId = msg.originId;
                maandData[maand][originId] = (maandData[maand][originId] || 0) + 1; // tellen hoe vaak een bepaald originId voorkomt. eerste keer? 0 dan +1
            });

            const labels = Object.keys(maandData).sort(); // x-as
            const duplicateIds = []; // berichten (originId's) >1 keer
            const uniekeIds = [];      // berichten (originId's) = 1 keer

            // per maand berekenen hoeveel ids er 1x of vaker zijn
            labels.forEach(maand => {
                let eenKeer = 0;
                let vaker = 0;
                Object.values(maandData[maand]).forEach(count => {
                    if (count === 1) eenKeer++;
                    else vaker++;
                });
                duplicateIds.push(vaker);
                uniekeIds.push(eenKeer);
            });

            const ctx = canvas.getContext("2d");

            idBarCharts[canvasId] = new Chart(ctx, {
                plugins: [ChartDataLabels],
                type: "bar",
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: "Berichten naar meerdere frames",
                            data: duplicateIds,
                            backgroundColor: "rgba(75, 192, 192, 0.7)",
                            stack: "stack1"
                        },
                        {
                            label: "Berichten naar 1 frame",
                            data: uniekeIds,
                            backgroundColor: "rgba(255, 159, 64, 0.7)",
                            stack: "stack1"
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        datalabels: {
                            display: function(context) {
                                return context.datasetIndex === context.chart.data.datasets.length - 1;
                            },
                            formatter: function(value, context) { //voor totaal per bar
                                const datasets = context.chart.data.datasets;
                                const index = context.dataIndex;
                                let sum = 0;
                                datasets.forEach(ds => {
                                    sum += ds.data[index];
                                });
                                return sum;
                            },
                            anchor: 'end',
                            align: 'top',
                            font: {
                                weight: 'bold'
                            }
                        },
                        legend: { position: "bottom" },
                        title: {
                            display: true,
                            text: "Aantal berichten die naar meerdere frames zijn gestuurd vs naar 1 frame (per maand)",
                            padding: {
                                bottom: 20
                            }
                        }
                    },
                    scales: {
                        x: { stacked: true },
                        y: { stacked: true, beginAtZero: true }
                    },
                },
            });
        });
}

function avgCommunityCall(canvasId){
    fetch("/api/communityCalls")
        .then(response => response.json())
        .then(data => {
            const calls = data.data.calls;
            const canvas = document.getElementById(canvasId);
            const melding = document.getElementById("geenDataMeldingCall");

            // Update de titel boven de chart
            document.getElementById("callTitle").textContent = "Gemiddelde duur van (video)call met zorgprofessional";
            document.getElementById("subTitle").textContent= "(Uitschieters uitgefilterd: 1% kortste en langste duur)"

            // Als geen data beschikbaar is
            if (!calls|| calls.length === 0) {
                canvas.style.display = "none";
                melding.style.display = "block";
                return;
            }



            // Haal 'started' en 'ended' waarden op en bereken de duur van elk gesprek
            const durations = calls.map(call => {
                const started = new Date(call.started);
                const ended = new Date(call.ended);
                return (ended - started) / 1000; // in seconden
            });

            // Sorteer de durations array
            durations.sort((a, b) => a - b);

            // Bereken het aantal datums om eruit te halen (1%)
            const numberOfCalls = durations.length;
            const removeCount = Math.floor(numberOfCalls * 0.01);

            // Verwijder de eerste 1% en laatste 1%
            const filteredDurations = durations.slice(removeCount, numberOfCalls - removeCount);

            // Bereken het gemiddelde
            const totaal = filteredDurations.reduce((sum, dur) => sum + dur, 0);
            const gemiddelde = totaal / filteredDurations.length;

            // Toont gemiddelde in minuten en seconden
            const gemMinuten = Math.floor(gemiddelde / 60);
            const gemSeconden = Math.round(gemiddelde % 60);
            canvas.innerText = `${gemMinuten} minuten en ${gemSeconden} seconden`;
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

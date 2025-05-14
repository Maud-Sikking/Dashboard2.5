/* ===== Inlogfunctionaliteit ===== */

// URL van Hasura endpoint
const HASURA_URL = "https://jouw-hasura-endpoint/v1/graphql";

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

/* ===== Patiëntenfunctionaliteit ===== */

function loadPatients() {
    $.getJSON("/devices", function (data) {
        console.log("Patiënten geladen:", data);
        let patients = normaliseerGenders(data.database_patient);

        $('#patienten').DataTable({
            data: patients,
            columns: [
                { data: 'patient_id' },
                { data: 'voornamen' },
                { data: 'achternaam' },
                { data: 'initialen' },
                { data: 'roepnaam' },
                { data: 'geboortedatum' },
                { data: 'emailadres' },
                { data: 'straatnaam' },
                { data: 'huisnummer' },
                { data: 'huisnummertoevoeging' },
                { data: 'postcode' },
                { data: 'woonplaats' },
                { data: 'iq' },
                { data: 'genderidentiteit' }
            ]
        })
    });
}
loadPatients()

function loadalarms() {
    $.getJSON("/alarms", function (data) {
        console.log("Alarm device geladen:", data);
        let patients = normaliseerGenders(data.database_patient);

        $('#patienten').DataTable({
            data: patients,
            columns: [
                { data: 'patient_id' },
                { data: 'voornamen' },
                { data: 'achternaam' },
                { data: 'initialen' },
                { data: 'roepnaam' },
                { data: 'geboortedatum' },
                { data: 'emailadres' },
                { data: 'straatnaam' },
                { data: 'huisnummer' },
                { data: 'huisnummertoevoeging' },
                { data: 'postcode' },
                { data: 'woonplaats' },
                { data: 'iq' },
                { data: 'genderidentiteit' }
            ]
        })
    });
}
loadalarms()


function getFullName(patient) {
    let fullname = `${patient.voornamen} `;
    if (patient.tussenvoegsel) fullname += `${patient.tussenvoegsel} `;
    return fullname + patient.achternaam;
}

function getPatientIdSelectBox() {
    $.getJSON("/patient/getAll", function(data) {
        let patients = normaliseerGenders(data.database_patient);
        let selectCode = "<option>Selecteer een patiënt</option>";

        patients.forEach((patient) => {
            let patientText = `${getFullName(patient)} - ID: ${patient.patient_id}`;
            selectCode += `<option value="${patient.patient_id}">${patientText}</option>`;
        });

        document.getElementById("patient_id").innerHTML = selectCode;
    });
}

function showPatientData() {
    event.preventDefault();
    let form = document.getElementById("select-patient-form");
    let formdata = new FormData(form);
    let patient_id = formdata.get("patient_id");

    console.log(patient_id);

    let form2 = document.getElementById("edit-patient-form");
    form2.style.display = "block";

    // Load patient data
    $.getJSON(`/patient/get`, { id: patient_id }, function (data) {
        console.log("Response van backend:", data);
        let patient = data.database_patient_by_pk;
        console.log("Patiëntgegevens gevonden:", patient);
        document.getElementById("voornamen").value = patient.voornamen || "";
        document.getElementById("achternaam").value = patient.achternaam || "";
        document.getElementById("initialen").value = patient.initialen || "";
        document.getElementById("roepnaam").value = patient.roepnaam || "";
        document.getElementById("geboortedatum").value = patient.geboortedatum || "";
        document.getElementById("emailadres").value = patient.emailadres || "";
        document.getElementById("straatnaam").value = patient.straatnaam || "";
        document.getElementById("huisnummer").value = patient.huisnummer || "";
        document.getElementById("huisnummertoevoeging").value = patient.huisnummertoevoeging || "";
        document.getElementById("postcode").value = patient.postcode || "";
        document.getElementById("woonplaats").value = patient.woonplaats || "";
        document.getElementById("iq").value = patient.iq || "";
        document.getElementById("genderidentiteit").value = SNOMEDGenderToString(patient.genderidentiteit || "").split(" ")[0].toLowerCase();

    })
    //     .fail(function (jqXHR, textStatus, errorThrown) {
    //     console.error("Fout bij ophalen patiëntgegevens:", textStatus, errorThrown);
    //     alert("Er is een fout opgetreden bij het ophalen van de patiëntgegevens.");
    // });
}

function normaliseerGenders(patienten) {
    patienten.forEach((patient) => patient.genderidentiteit = SNOMEDGenderToString(patient.genderidentiteit));
    return patienten;
}

function SNOMEDGenderToString(SNOMED) {
    switch (SNOMED) {
        case 446141000124107:
            return `Vrouw (${SNOMED})`;
        case 446151000124109:
            return `Man (${SNOMED})`;
        case 33791000087105:
            return `Non-binair (${SNOMED})`;
        default:
            return "Geen gender gespecificeerd";
    }
}

function genderToSNOMED(gender) {
    switch (gender.toLowerCase()) {
        case "man":
            return 446151000124109;
        case "vrouw":
            return 446141000124107;
        case "non-binair":
            return 33791000087105;
        default:
            return null;
    }
}

function extractPatientDataFromForm(form){
    formdata = new FormData(form)

    patient = {
        "voornamen": formdata.get("voornamen"),
        "achternaam": formdata.get("achternaam"),
        "initialen": formdata.get("initialen"),
        "roepnaam": formdata.get("roepnaam"),
        "geboortedatum": formdata.get("geboortedatum"),
        "emailadres": formdata.get("emailadres"),
        "straatnaam": formdata.get("straatnaam"),
        "huisnummer": formdata.get("huisnummer"),
        "huisnummertoevoeging": formdata.get("huisnummertoevoeging"),
        "postcode": formdata.get("postcode"),
        "woonplaats": formdata.get("woonplaats"),
        "iq": formdata.get("iq"),
        "genderidentiteit": genderToSNOMED(formdata.get("genderidentiteit"))
    };
    return patient;
}

function addPatient(){
    event.preventDefault() // voorkomt dat er een lege pagina wordt geladen (default behaviour van submit)
    form = document.getElementById("add-patient-form")
    patient = extractPatientDataFromForm(form)
    $.getJSON("/patient/new", patient, function(data){
        console.log(data)
        window.location.href="./patient.html"
    })
}
function editPatient(){
    event.preventDefault() // voorkomt dat er een lege pagina wordt geladen (default behaviour van submit)
    form = document.getElementById("edit-patient-form")
    patient = extractPatientDataFromForm(form)
    form = document.getElementById("select-patient-form")
    formdata = new FormData(form)
    patient.id = formdata.get("patient_id")
    console.log(patient)
    $.getJSON("/patient/update", patient, function(data){
        console.log(data)
        window.location.href="./patient.html"
    })
}
function deletePatient(){
    event.preventDefault()
    form = document.getElementById("select-patient-form")
    formdata = new FormData(form)
    id = formdata.get("patient_id")
    console.log("delete_id:" + id)
    $.getJSON("/patient/delete/:patient_id", {id: id}, function(data){
        console.log("delete data:" + data)
        window.location.href="./patient.html"
    })
}
function loadbehandelingen(){
    $.getJSON("/behandeling/getAll", function(data){
        console.log("test")
        console.log(data)
        behandeling = data.database_behandeling
        $('#behandeling').DataTable({
            data: behandeling,
            columns: [
                {data: 'behandeling_id'},
                {data: 'patient_id'},
                {data: 'beeindiging_reden'},
                {data: 'behandeling_eind'},
                {data: 'behandeling_besluit'},
                {data: 'behandeling_begin'},
                {data: 'recentste_afspraak'},
                {data: 'behandelsoort'},
                {data: 'medewerker_id'}
            ]
        })
    })
}
function loaddiagnoses() {
    $.getJSON("/diagnose/getAll", function (data) {
        console.log("test")
        console.log(data)
        diagnose = data.database_diagnose
        $('#diagnose').DataTable({
            data: diagnose,
            columns: [
                {data: 'diagnose_id'},
                {data: 'patient_id'},
                {data: 'diagnose_datum'},
                {data: 'diagnose_naam'},
                {data: 'status_id'},
                {data: 'diagnose_toelichting'}
            ]
        })
    })
}
function loadggzinstelling(){
    $.getJSON("/ggz_instelling/getAll", function(data){
        console.log("test")
        console.log(data)
        ggzinstelling = data.database_ggz_instelling
        $('#ggzinstelling').DataTable({
            data: ggzinstelling,
            columns: [
                {data: 'ggz_id'},
                {data: 'naam'},
                {data: 'straatnaam'},
                {data: 'huisnummer'},
                {data: 'toevoeging'},
                {data: 'stad'},
                {data: 'postcode'}
            ]
        })
    })
}
function loadggzmedewerker(){
    $.getJSON("/ggzmedewerker/getAll", function(data){
        console.log("test")
        console.log(data)
        ggzmedewerker = data.database_ggz_medewerker
        $('#ggzmedewerker').DataTable({
            data: ggzmedewerker,
            columns: [
                {data: 'medewerker_id'},
                {data: 'specialisme'},
                {data: 'initialen'},
                {data: 'voornamen'},
                {data: 'achternaam'},
                {data: 'roepnaam'}
            ]
        })
    })
}
function loadKlacht(){
    $.getJSON("/klacht/getAll", function(data){
        console.log("test")
        console.log(data)
        klacht = data.database_klacht
        $('#klacht').DataTable({
            data: klacht,
            columns: [
                {data: 'klacht_id'},
                {data: 'patient_id'},
                {data: 'klacht_begin'},
                {data: 'klacht_eind'},
                {data: 'klacht_soort'},
                {data: 'status_id'},
                {data: 'klacht_toelichting'}
            ]
        })
    })
}
function loadTherapie(){
    $.getJSON("/therapie/getAll", function(data){
        console.log("test")
        console.log(data)
        therapie = normaliseerTherapieType(data.database_therapie)
        $('#therapie').DataTable({
            data: therapie,
            columns: [
                {data: 'therapie_id'},
                {data: 'behandeling_id'},
                {data: 'therapie_begin'},
                {data: 'therapie_eind'},
                {data: 'therapie_type'},
                {data: 'therapie_toelichting'}
            ]
        })
    })
}
function normaliseerTherapieType(typen) {
    typen.forEach((patient) => patient.therapie_type = SNOMEDTherapieToString(patient.therapie_type))
    return typen
}
function SNOMEDTherapieToString(SNOMED){
    switch(SNOMED){
        case 228557008:
            return `Cognitieve en gedragstherapie (${SNOMED})`
        case 449030000:
            return `Eye movement desensitization and reprocessing (${SNOMED})`
        case 75516001:
            return `Psychotherapie (${SNOMED})`
        default:
            return "None"
    }
}
function loadmedicatietoediening(){
    $.getJSON("/medicatietoediening/getAll", function(data){
        console.log("test")
        console.log(data)
        medicatietoediening = data.database_medicatietoediening
        $('#medicatietoediening').DataTable({
            data: medicatietoediening,
            columns: [
                {data: 'medicatie_id'},
                {data: 'behandeling_id'},
                {data: 'medicijn_naam'},
                {data: 'dosering'},
                {data: 'toediening_begin'},
                {data: 'toediening_status'},
                {data: 'afwijkende_toediening'},
                {data: 'toediening_eind'}
            ]
        })
    })
}
function loadTelefoonnummers(){
    $.getJSON("/telefoonnummers/getAll", function(data){
        console.log("test")
        console.log(data)
        telefoonnummers= data.database_telefoonnummers
        $('#telefoonnummers').DataTable({
            data: telefoonnummers,
            columns: [
                {data: 'patient_id'},
                {data: 'telefoonnummer'},
                {data: 'telefoon_id'}
            ]
        })
    })
}
function loadwerkzaam_bij(){
    $.getJSON("/werkzaam_bij/getAll", function(data){
        console.log("test")
        console.log(data)
        werkzaam_bij = data.database_werkzaam_bij
        $('#werkzaam_bij').DataTable({
            data: werkzaam_bij,
            columns: [
                {data: 'ggz_id'},
                {data: 'medewerker_id'}
            ]
        })
    })
}
// De rest van je bestaande functies blijven hetzelfde
function inloggegevensChecken() {
    // Verkrijg de gebruikersnaam en wachtwoord van de inlogvelden
    const gebruikersnaam = document.getElementById('username').value;
    const wachtwoord = document.getElementById('password').value;

    $.getJSON("/patient/new", patient, function (data) {
        console.log("Nieuwe patiënt toegevoegd:", data);
        window.location.href = "./patient.html";
    });
}

function editPatient(event) {
    event.preventDefault(); // voorkomt dat er een lege pagina wordt geladen (default behaviour van submit)
    let form = document.getElementById("edit-patient-form");
    let patient = extractPatientDataFromForm(form);

    let form2 = document.getElementById("select-patient-form");
    let formdata = new FormData(form2);
    patient.id = formdata.get("patient_id");

    console.log("Gegevens van patiënt bijgewerkt:", patient);

    $.getJSON("/patient/update", patient, function (data) {
        console.log("Patiënt gegevens succesvol geüpdatet:", data);
        window.location.href = "./patient.html";
    });
}

// function deletePatient(event) {
//     event.preventDefault(); // voorkomt dat er een lege pagina wordt geladen (default behaviour van submit)
//
// }

// URL van Hasura endpoint
const HASURA_URL = "https://jouw-hasura-endpoint/v1/graphql";

// Hasura query: data aantal alerts per device van december
fetch('/gemAlertsDec')
    .then(response => response.json())
    .then(data => {
        const devices = data.data.devices;
        // verwerk devices voor Chart.js
    });




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

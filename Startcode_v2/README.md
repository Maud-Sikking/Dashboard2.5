# Startcode project
Dit is de startcode van het project van MI-X 2.2 2024-2025. Het project bevat de code die nodig is om de applicatie te runnen en voorbeeldcode voor een patiënt tabel. Deze tabel komt niet overeen met jullie patiënt tabel en de code dient dan ook alleen als startpunt voor jullie werk.

Voordat je het project kunt gebruiken:
1. Verbind je database met Hasura-cloud: https://cloud.hasura.io/. Zie [Canvas](https://canvas.uva.nl/courses/45642/pages/hasura-cloud) voor een handleiding
2. Vul application.properties (onder resources) aan met jullie API link en secret. Het secret kun je vinden onder de settings van jullie Hasura project, onder Env vars -> HASURA_GRAPHQL_ADMIN_SECRET. Verander de naamgeving in het application.properties bestand niet, alleen de waardes

Algemene opmerkingen:
1. Programmeren doen we in de src folder, niet in build/target/etc.
2. Als je gebruik maakt van een andere API dan Hasura, bijvoorbeeld voor SNOMED, dan maak je daar een nieuwe Java class voor aan. Kijk daarvoor bijvoorbeeld naar de PostcodeApiController.java
3. Code met betrekking tot de Hasura API staat in HasuraApiController.java. Het is de bedoeling dat je hier aan het werk gaat
4. Javascript code staat in script.js. Deze kun je vinden onder resources/static. De code zal niet 100% passen voor jullie casus, maar is hopelijk een mooi startpunt
5. Nieuwe html bestanden zet je onder static. Hier mag je een nieuwe map voor aanmaken als je dat mooier vindt

Informatie over de layout van onze voorbeeld database en Hasura API kun je op [Canvas](https://canvas.uva.nl/courses/45642/pages/project-voorbeeld-database-en-hasura-api) vinden
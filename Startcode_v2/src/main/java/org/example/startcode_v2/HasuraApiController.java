package org.example.startcode_v2;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
public class HasuraApiController extends ApiController {
  // De @Value injectors hieronder zorgen ervoor dat je de gegevens uit appplication.properties als veld kunt opslaan en gebruiken binnen deze class.
  @Value("${hasura.baseURL}")
  private String BASE_URL;
  @Value("${hasura.secret}")
  private String SECRET;
  private static final String PATIENT_MAPPING = "patient";

  @Autowired
  private RestTemplate restTemplate;



  @PostMapping("/gemAlertsDec")
  public ResponseEntity<String> getDevices() {
    String query = """
        {
          "query": "query CountDevicesWith10to30Alerts { alerts_per_device_november_2024_aggregate(where: { alert_count: { _gte: 10, _lte: 30 } }) { aggregate { count } } }"
        }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", SECRET);

    HttpEntity<String> request = new HttpEntity<>(query, headers);

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;
  }


  @GetMapping("/patient/getAll")
  public ResponseEntity<String> getAllPatients() {
    try {
      String completeURL = BASE_URL + PATIENT_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  @GetMapping("/patient/get")
  public ResponseEntity<String> getPatient(@RequestParam(value = "id") Integer patientId) {
    System.out.println("test");
    try {
      String completeURL = BASE_URL + PATIENT_MAPPING + "/" + patientId;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con);
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  @GetMapping("/patient/delete")
  public ResponseEntity<String> deletePatient(@RequestParam(value = "id") Integer patientId) {
    try {
      String completeURL = BASE_URL + PATIENT_MAPPING + "delete/" + patientId;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con);
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  @GetMapping("/patient/new")
  public ResponseEntity<String> addPatient(@RequestParam(value = "voornamen") String voornamen,
                                           @RequestParam(value = "achternaam") String achternaam,
                                           @RequestParam(value = "initialen") String initialen,
                                           @RequestParam(value = "roepnaam") String roepnaam,
                                           @RequestParam(value = "geboortedatum") String geboortedatum,
                                           @RequestParam(value = "emailadres") String emailadres,
                                           @RequestParam(value = "straatnaam") String straatnaam,
                                           @RequestParam(value = "huisnummer") int huisnummer,
                                           @RequestParam(value = "huisnummertoevoeging") String huisnummertoevoeging,
                                           @RequestParam(value = "postcode") String postcode,
                                           @RequestParam(value = "woonplaats") String woonplaats,
                                           @RequestParam(value = "iq") int iq,
                                           @RequestParam(value = "genderidentiteit") long genderidentiteit) {
    try {
      JSONObject object = new JSONObject();
      object.put("voornamen", voornamen);
      object.put("achternaam", achternaam);
      object.put("initialen", initialen);
      object.put("roepnaam", roepnaam);
      object.put("geboortedatum", geboortedatum);
      object.put("emailadres", emailadres);
      object.put("straatnaam", straatnaam);
      object.put("huisnummer", huisnummer);
      object.put("huisnummertoevoeging", huisnummertoevoeging);
      object.put("postcode", postcode);
      object.put("woonplaats", woonplaats);
      object.put("iq", iq);
      object.put("genderidentiteit", genderidentiteit);
      JSONObject superObject = new JSONObject();
      superObject.put("object", object);
      String jsonInputString = superObject.toString();;

      URL url = new URI(BASE_URL + PATIENT_MAPPING + "/new2").toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      try (OutputStream os = con.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
        os.close();
      }

      return getApiCallResponse(con);
    } catch (URISyntaxException | IOException | RuntimeException | JSONException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  @GetMapping("/patient/update")
  public ResponseEntity<String> updatePatient(@RequestParam(value = "voornamen") String voornamen,
                                              @RequestParam(value = "achternaam") String achternaam,
                                              @RequestParam(value = "initialen") String initialen,
                                              @RequestParam(value = "roepnaam") String roepnaam,
                                              @RequestParam(value = "geboortedatum") String geboortedatum,
                                              @RequestParam(value = "emailadres") String emailadres,
                                              @RequestParam(value = "straatnaam") String straatnaam,
                                              @RequestParam(value = "huisnummer") int huisnummer,
                                              @RequestParam(value = "huisnummertoevoeging") String huisnummertoevoeging,
                                              @RequestParam(value = "postcode") String postcode,
                                              @RequestParam(value = "woonplaats") String woonplaats,
                                              @RequestParam(value = "iq") int iq,
                                              @RequestParam(value = "genderidentiteit") long genderidentiteit, @RequestParam(value = "patient_id") int patientId){
    System.out.println("test update");
    try {
      JSONObject object = new JSONObject();
      object.put("voornamen", voornamen);
      object.put("achternaam", achternaam);
      object.put("initialen", initialen);
      object.put("roepnaam", roepnaam);
      object.put("geboortedatum", geboortedatum);
      object.put("emailadres", emailadres);
      object.put("straatnaam", straatnaam);
      object.put("huisnummer", huisnummer);
      object.put("huisnummertoevoeging", huisnummertoevoeging);
      object.put("postcode", postcode);
      object.put("woonplaats", woonplaats);
      object.put("iq", iq);
      object.put("genderidentiteit", genderidentiteit);
      JSONObject superObject = new JSONObject();
      superObject.put("object", object);
      superObject.put("patient_id", patientId);
      String jsonInputString = superObject.toString();
      System.out.println(jsonInputString);

      URL url = new URI(BASE_URL + PATIENT_MAPPING + "/update").toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      try (OutputStream os = con.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
        os.close();
      }

      return getApiCallResponse(con);
    } catch (URISyntaxException | IOException | RuntimeException | JSONException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String BEHANDELING_MAPPING = "behandeling";

  @GetMapping("/behandeling/getAll")
  public ResponseEntity<String> getAllbehandelingen() {
    try {
      String completeURL = BASE_URL + BEHANDELING_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String DIAGNOSE_MAPPING = "diagnose";

  @GetMapping("/diagnose/getAll")
  public ResponseEntity<String> getAlldiagnoses() {
    try {
      String completeURL = BASE_URL + DIAGNOSE_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String GGZINSTELLING_MAPPING = "ggz_instelling";
  @GetMapping("/ggz_instelling/getAll")
  public ResponseEntity<String> getAllGgzinstellingen() {
    try {
      String completeURL = BASE_URL + GGZINSTELLING_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String GGZMEDEWERKER_MAPPING = "ggz_medewerker";
  @GetMapping("/ggzmedewerker/getAll")
  public ResponseEntity<String> getAllGgzmedewerkers() {
    try {
      String completeURL = BASE_URL + GGZMEDEWERKER_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String KLACHT_MAPPING = "klacht";
  @GetMapping("/klacht/getAll")
  public ResponseEntity<String> getAllklachten() {
    try {
      String completeURL = BASE_URL + KLACHT_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String THERAPIE_MAPPING = "therapie";

  @GetMapping("/therapie/getAll")
  public ResponseEntity<String> getAlltherapieen() {
    try {
      String completeURL = BASE_URL + THERAPIE_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String MEDICATIE_MAPPING = "medicatietoediening";

  @GetMapping("/medicatietoediening/getAll")
  public ResponseEntity<String> getAllmedicatietoediening() {
    try {
      String completeURL = BASE_URL + MEDICATIE_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String DIAGNOSTICERING_MAPPING = "diagnosticering";
  @GetMapping("/diagnosticering/getAll")
  public ResponseEntity<String> getAlldiagnosticeringen() {
    try {
      String completeURL = BASE_URL + DIAGNOSTICERING_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String TELEFOONNUMMERS_MAPPING = "telefoonnummers";
  @GetMapping("/telefoonnummers/getAll")
  public ResponseEntity<String> getAlltelefoonnummers() {
    try {
      String completeURL = BASE_URL + TELEFOONNUMMERS_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

  private static final String WERKZAAMBIJ_MAPPING = "werkzaam_bij";
  @GetMapping("/werkzaam_bij/getAll")
  public ResponseEntity<String> getAllwerkzaambij() {
    try {
      String completeURL = BASE_URL + WERKZAAMBIJ_MAPPING;
      System.out.println(completeURL);
      URL url = new URI(completeURL).toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("x-hasura-admin-secret", SECRET);
      con.setDoOutput(true);

      return getApiCallResponse(con); // Hier klaagt IntelliJ wel eens over dat de ApiController class niet gevonden kan worden. Als je vervolgens de applicatie runt, is dat succesvol (errors blijven)
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }

}
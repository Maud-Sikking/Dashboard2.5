package org.example.startcode_v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HasuraApiController extends ApiController {
  // De @Value injectors hieronder zorgen ervoor dat je de gegevens uit appplication.properties als veld kunt opslaan en gebruiken binnen deze class.
  @Value("${hasura.baseURL}")
  private String BASE_URL;
  @Value("${hasura.secret}")
  private String SECRET;

  @Autowired
  private RestTemplate restTemplate;


  @GetMapping("/gemAlertsDec")
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

  @GetMapping("/api/alerts-per-categorie-5")
  public ResponseEntity<String> alertsPerCategorie5() {
    String query = """
    {
      group1: alerts_per_device_januari_2025_aggregate(where: { alert_count: { _lte: 10 } }) {
        aggregate { count }
      }
      group2: alerts_per_device_januari_2025_aggregate(where: { alert_count: { _gt: 10, _lte: 30 } }) {
        aggregate { count }
      }
      group3: alerts_per_device_januari_2025_aggregate(where: { alert_count: { _gt: 30, _lte: 50 } }) {
        aggregate { count }
      }
      group4: alerts_per_device_januari_2025_aggregate(where: { alert_count: { _gt: 50, _lte: 70 } }) {
        aggregate { count }
      }
      group5: alerts_per_device_januari_2025_aggregate(where: { alert_count: { _gt: 70 } }) {
        aggregate { count }
      }
    }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", SECRET);

    HttpEntity<String> request = new HttpEntity<>("{\"query\": \"" + query.replace("\"", "\\\"").replace("\n", "") + "\"}", headers);

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;
  }

  @GetMapping("/api/alerts-per-categorie-5-februari")
  public ResponseEntity<String> alertsPerCategorie5Februari() {
    String query = """
    {
      group1: alerts_per_device_februari_2025_aggregate(where: { alert_count: { _lte: 10 } }) {
        aggregate { count }
      }
      group2: alerts_per_device_februari_2025_aggregate(where: { alert_count: { _gt: 10, _lte: 30 } }) {
        aggregate { count }
      }
      group3: alerts_per_device_februari_2025_aggregate(where: { alert_count: { _gt: 30, _lte: 50 } }) {
        aggregate { count }
      }
      group4: alerts_per_device_februari_2025_aggregate(where: { alert_count: { _gt: 50, _lte: 70 } }) {
        aggregate { count }
      }
      group5: alerts_per_device_februari_2025_aggregate(where: { alert_count: { _gt: 70 } }) {
        aggregate { count }
      }
    }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", SECRET);

    HttpEntity<String> request = new HttpEntity<>("{\"query\": \"" + query.replace("\"", "\\\"").replace("\n", "") + "\"}", headers);

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;
  }

  @GetMapping("/api/alerts-soorten-januari")
  public ResponseEntity<String> alertsSoortenJanuari() {
    String query = """
    {
      soort_alert_januari_2025 {
        alert_count
        type
      }
    }
    """;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", SECRET);

    HttpEntity<String> request = new HttpEntity<>(
            "{\"query\": \"" + query.replace("\"", "\\\"").replace("\n", "") + "\"}",
            headers
    );

    return restTemplate.exchange(BASE_URL, HttpMethod.POST, request, String.class);
  }


}
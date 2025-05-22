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

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;

  }

  @GetMapping("/api/alerts-soorten-februari")
  public ResponseEntity<String> alertsSoortenFebruari() {
    String query = """
    {
      soort_alert_februari_2025 {
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

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;
  }

  @GetMapping("/api/alerts-soorten-maart")
  public ResponseEntity<String> alertsSoortenMaart() {
    String query = """
    {
      soort_alert_maart_2025 {
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

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;
  }

  @GetMapping("/api/origin-ids-per-maand")
  public ResponseEntity<String> originIdPerMaand() {
    String query = """
    {
      decemberMeerdereFrames: meerdere_originids_december_2024_aggregate {aggregate {count}}
      decemberTotaal: messages_aggregate(distinct_on: originId
               where: {sent: {_gte: "2024-12-01T00:00:00", _lt: "2025-01-01T00:00:00"}}) {aggregate { count }}
      januariMeerdereFrames: meerdere_originids_januari_2025_aggregate {aggregate {count}}
      januariTotaal: messages_aggregate(distinct_on: originId
               where: {sent: {_gte: "2025-01-01T00:00:00", _lt: "2025-02-01T00:00:00"}}) {aggregate { count }}
      februariMeerdereFrames: meerdere_originids_februari_2025_aggregate {aggregate {count}}
      februariTotaal: messages_aggregate(distinct_on: originId
               where: {sent: {_gte: "2025-02-01T00:00:00", _lt: "2025-03-01T00:00:00"}}) {aggregate { count }}
      maartMeerdereFrames: meerdere_originids_maart_2025_aggregate {aggregate {count}}
      maartTotaal: messages_aggregate(distinct_on: originId
               where: {sent: {_gte: "2025-03-01T00:00:00", _lt: "2025-04-01T00:00:00"}}) {aggregate { count }}
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

  @GetMapping("/api/alert-type-answer-no")
  public ResponseEntity<String> alertsPerMaandTrend() {
    String query = """
    {
      oktober1: alerts_aggregate(where: {created: {_gte: "2024-10-01T00:00:00", _lte: "2024-11-01T00:00:00"}, frameUUID: {_eq: "760e1d27-f2ad-43fc-880b-3bd810f7b705"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      november1: alerts_aggregate(where: {created: {_gte: "2024-11-01T00:00:00", _lte: "2024-12-01T00:00:00"}, frameUUID: {_eq: "760e1d27-f2ad-43fc-880b-3bd810f7b705"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      december1: alerts_aggregate(where: {created: {_gte: "2024-12-01T00:00:00", _lte: "2025-01-01T00:00:00"}, frameUUID: {_eq: "760e1d27-f2ad-43fc-880b-3bd810f7b705"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      januari1: alerts_aggregate(where: {created: {_gte: "2025-01-01T00:00:00", _lte: "2025-02-01T00:00:00"}, frameUUID: {_eq: "760e1d27-f2ad-43fc-880b-3bd810f7b705"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      februari1: alerts_aggregate(where: {created: {_gte: "2025-02-01T00:00:00", _lte: "2025-03-01T00:00:00"}, frameUUID: {_eq: "760e1d27-f2ad-43fc-880b-3bd810f7b705"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      maart1: alerts_aggregate(where: {created: {_gte: "2025-03-01T00:00:00", _lte: "2025-04-01T00:00:00"}, frameUUID: {_eq: "760e1d27-f2ad-43fc-880b-3bd810f7b705"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      oktober2: alerts_aggregate(where: {created: {_gte: "2024-10-01T00:00:00", _lte: "2024-11-01T00:00:00"}, frameUUID: {_eq: "11f5aa26-4964-49df-ac2b-83354cbd518a"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      november2: alerts_aggregate(where: {created: {_gte: "2024-11-01T00:00:00", _lte: "2024-12-01T00:00:00"}, frameUUID: {_eq: "11f5aa26-4964-49df-ac2b-83354cbd518a"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      december2: alerts_aggregate(where: {created: {_gte: "2024-12-01T00:00:00", _lte: "2025-01-01T00:00:00"}, frameUUID: {_eq: "11f5aa26-4964-49df-ac2b-83354cbd518a"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      januari2: alerts_aggregate(where: {created: {_gte: "2025-01-01T00:00:00", _lte: "2025-02-01T00:00:00"}, frameUUID: {_eq: "11f5aa26-4964-49df-ac2b-83354cbd518a"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      februari2: alerts_aggregate(where: {created: {_gte: "2025-02-01T00:00:00", _lte: "2025-03-01T00:00:00"}, frameUUID: {_eq: "11f5aa26-4964-49df-ac2b-83354cbd518a"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      maart2: alerts_aggregate(where: {created: {_gte: "2025-03-01T00:00:00", _lte: "2025-04-01T00:00:00"}, frameUUID: {_eq: "11f5aa26-4964-49df-ac2b-83354cbd518a"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      oktober3: alerts_aggregate(where: {created: {_gte: "2024-10-01T00:00:00", _lte: "2024-11-01T00:00:00"}, frameUUID: {_eq: "cffe2ed4-2c15-43c8-ae80-e705c2560438"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      november3: alerts_aggregate(where: {created: {_gte: "2024-11-01T00:00:00", _lte: "2024-12-01T00:00:00"}, frameUUID: {_eq: "cffe2ed4-2c15-43c8-ae80-e705c2560438"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      december3: alerts_aggregate(where: {created: {_gte: "2024-12-01T00:00:00", _lte: "2025-01-01T00:00:00"}, frameUUID: {_eq: "cffe2ed4-2c15-43c8-ae80-e705c2560438"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      januari3: alerts_aggregate(where: {created: {_gte: "2025-01-01T00:00:00", _lte: "2025-02-01T00:00:00"}, frameUUID: {_eq: "cffe2ed4-2c15-43c8-ae80-e705c2560438"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      februari3: alerts_aggregate(where: {created: {_gte: "2025-02-01T00:00:00", _lte: "2025-03-01T00:00:00"}, frameUUID: {_eq: "cffe2ed4-2c15-43c8-ae80-e705c2560438"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
      maart3: alerts_aggregate(where: {created: {_gte: "2025-03-01T00:00:00", _lte: "2025-04-01T00:00:00"}, frameUUID: {_eq: "cffe2ed4-2c15-43c8-ae80-e705c2560438"}, type: {_eq: "MESSAGE_MEDICINE_ANSWER_NO"}}) {
            aggregate {count}}
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

  @GetMapping("/api/communityCalls")
  public ResponseEntity<String> communityCalls() {
    String query = """
    {
      calls(where: {type: {_eq: "COMMUNITY"}, started: {_is_null: false}, ended: {_is_null: false}}) {
                  started
                  ended
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

    ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            request,
            String.class
    );

    return response;
  }

}
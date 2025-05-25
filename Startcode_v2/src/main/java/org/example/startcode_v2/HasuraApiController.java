package org.example.startcode_v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import org.springframework.web.bind.annotation.*;
import java.time.YearMonth;


@RestController
public class HasuraApiController extends ApiController {
  // De @Value injectors hieronder zorgen ervoor dat je de gegevens uit appplication.properties als veld kunt opslaan en gebruiken binnen deze class.
  @Value("${hasura.baseURL}")
  private String BASE_URL;
  @Value("${hasura.secret}")
  private String SECRET;

  @Autowired
  private RestTemplate restTemplate;

   @GetMapping("/api/devices")
  public ResponseEntity<String> devices(@RequestParam String month) {
    YearMonth ym = YearMonth.parse(month); // bijv. "2025-01"
    String start = ym.atDay(1).toString(); // "2025-01-01"
    String end = ym.plusMonths(1).atDay(1).toString(); // "2025-02-01"

    String query = """
    {
      devices(where: {
        firstRegistration: { _lt: "%s" },
        lastRegistration: { _gte: "%s" }
      }) {
        id
        frameDeviceIdentifier
      }
    }
    """.formatted(end, start);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", SECRET);

    String body = "{\"query\": \"" + query.replace("\"", "\\\"").replace("\n", "") + "\"}";
    HttpEntity<String> request = new HttpEntity<>(body, headers);

    return restTemplate.exchange(BASE_URL, HttpMethod.POST, request, String.class);
  }


  @GetMapping("/api/alerts")
  public ResponseEntity<String> alerts(@RequestParam String month) {
    YearMonth ym = YearMonth.parse(month); // "2025-01"
    String start = ym.atDay(1).toString(); // "2025-01-01"
    String end = ym.plusMonths(1).atDay(1).toString(); // "2025-02-01"

    String query = """
    {
      alerts(where: {
        created: { _gte: "%s", _lt: "%s" }
      }) {
        type
        frameUUID
      }
    }
    """.formatted(start, end);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", SECRET);

    String body = "{\"query\": \"" + query.replace("\"", "\\\"").replace("\n", "") + "\"}";
    HttpEntity<String> request = new HttpEntity<>(body, headers);

    return restTemplate.exchange(BASE_URL, HttpMethod.POST, request, String.class);
  }

  @GetMapping("/api/messages")
  public ResponseEntity<String> messages(@RequestParam String month) {
    YearMonth ym = YearMonth.parse(month); // "2025-01"
    String start = ym.minusYears(1).atDay(1).toString(); // "2024-01-01"
    String end = ym.plusMonths(1).atDay(1).toString(); // "2025-02-01"

    String query = """
{
  messages(where: {
    sent: { _gte: "%s", _lt: "%s" }
    
  }) {
    originId
    sent
  }
}
""".formatted(start, end);


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

}
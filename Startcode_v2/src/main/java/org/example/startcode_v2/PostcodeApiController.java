package org.example.startcode_v2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class PostcodeApiController extends ApiController {
  @GetMapping("/postcode")
  public ResponseEntity<String> postcode(@RequestParam(value = "postcode", defaultValue = "110AZ") String postcode,
      @RequestParam(name = "huisnummer", defaultValue = "9") String huisnummer) {

    try {
      String requestString = "https://uva.genuscare.works/api/sensordata/devices";
      requestString = requestString.concat(postcode).concat("&number=").concat(huisnummer);

      URL url = new URL(requestString);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      
      String token = "Bearer ";
      token = token.concat(System.getenv("postcodeApiKey"));
      con.setRequestProperty ("Authorization", token);
      
      con.setRequestMethod("GET");

      return getApiCallResponse(con);
    } catch (IOException e) {
      e.printStackTrace();

      // Throw a server error
      return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
    }
  }
}
package org.example.startcode_v2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@RestController
abstract class ApiController {
  protected ResponseEntity<String> getApiCallResponse(HttpURLConnection con) {
    int code = 501;

    try {
      con.connect();
      code = con.getResponseCode();

      try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        System.out.println(response.toString());
        return new ResponseEntity<String>(response.toString(), HttpStatus.valueOf(200));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Throw a server error
    return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(code));
  }
}
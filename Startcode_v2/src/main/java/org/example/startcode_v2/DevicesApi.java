package org.example.startcode_v2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@RestController
public class DevicesApi extends ApiController {
    @GetMapping("/devices")
    public ResponseEntity<String> devices() {

        try {
            String requestString = "https://uva.genuscare.works/api/sensordata/devices";

            URL url = new URL(requestString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String originalInput = "user:12885662-4814-426c-9f13-b877078215e2";
            String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
            con.setRequestProperty ("Authorization", "Basic "+ encodedString);

            con.setRequestMethod("GET");

            return getApiCallResponse(con);
        } catch (IOException e) {
            e.printStackTrace();

            // Throw a server error
            return new ResponseEntity<String>("FAIL", HttpStatus.valueOf(500));
        }
    }
}
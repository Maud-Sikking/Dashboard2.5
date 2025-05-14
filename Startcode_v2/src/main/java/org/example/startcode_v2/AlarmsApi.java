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
public class AlarmsApi extends ApiController {
    @GetMapping("/alarms")
    public ResponseEntity<String> alarms() {



        try {
            String requestString = "https://api.genus.care/statistics/229d3a81-a64e-4fb9-8b91-193b3b5f6105/alarms";

            URL url = new URL(requestString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJTdGF0aXN0aWNzIl0sImlzcyI6IkFDQ0VTUyIsInN1YiI6IkFQSVRPS0VOI2I5YWFmOGI4LTI0NzEtNDkzMi1hM2QxLTZiZGYzZDZhYzk3YyIsImlhdCI6MTc0NDk3OTA4OX0.oTlCP0ZFai0HDsg3c1o0xxsbsVgUMJGeOl7bF036CaI";
            token = token.concat(System.getenv("alarmsApiKey"));
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
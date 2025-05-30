package org.example.startcode_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class StartcodeV2Application {
    // TODO: Deze class runnen om de applicatie te starten. Deze start vervolgens op localhost:8080

    public static void main(String[] args) {
        SpringApplication.run(StartcodeV2Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

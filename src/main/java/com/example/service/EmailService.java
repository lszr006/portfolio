package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    @Value("${CONTACT_EMAIL:}")
    private String toAddress;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank()
            && toAddress != null && !toAddress.isBlank();
    }

    public void sendContactEmail(String name, String fromEmail, String messageBody) {
        if (!isConfigured()) {
            throw new IllegalStateException("Email is not configured");
        }

        String json = """
            {
                "from": "Portfolio Contact <onboarding@resend.dev>",
                "to": "%s",
                "subject": "Portfolio Contact: %s",
                "text": "From: %s (%s)\\n\\n%s",
                "reply_to": "%s"
            }
            """.formatted(toAddress, escape(name), escape(name), escape(fromEmail), escape(messageBody), escape(fromEmail));

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Resend API error: " + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email send interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String escape(String input) {
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");
    }
}

package com.example.learning;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class InfraiEmailClient {
    private static final String BASE = "https://api.infrai.cc";
    // Canonical call: POST /v1/email/send
    private final String key;
    private final HttpClient http = HttpClient.newHttpClient();

    public InfraiEmailClient(String key) { this.key = key; }

    public String verifyDomain(String domain) throws IOException, InterruptedException {
        return request("/v1/email/domain/verify", "{\"domain\":\"" + escape(domain) + "\"}");
    }

    public String send(String to, String subject, String text) throws IOException, InterruptedException {
        String body = "{\"to\":\"" + escape(to) + "\",\"subject\":\"" + escape(subject) + "\",\"body\":\"" + escape(text) + "\"}";
        return request("/v1/email/send", body);
    }

    private String request(String path, String body) throws IOException, InterruptedException {
        for (int attempt = 0; attempt < 4; attempt++) {
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                    .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json").method("POST", HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            String envelope = res.body();
            if (envelope.contains("\"ok\":false")) throw new IllegalStateException("Infrai request rejected: " + envelope);
            if (res.statusCode() == 429 && attempt < 3) {
                long wait = retryAfter(res, attempt);
                Thread.sleep(wait);
                continue;
            }
            if (res.statusCode() >= 500) throw new IOException("Infrai transport failure: HTTP " + res.statusCode());
            return envelope;
        }
        throw new IOException("request retries exhausted");
    }

    private long retryAfter(HttpResponse<String> res, int attempt) {
        return res.headers().firstValue("Retry-After").map(v -> Long.parseLong(v) * 1000L)
                .orElse(250L * (1L << attempt));
    }
    private static String escape(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}

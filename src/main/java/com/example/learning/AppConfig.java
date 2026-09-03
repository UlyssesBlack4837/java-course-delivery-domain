package com.example.learning;

public record AppConfig(String apiKey, String learnerEmail, String educatorEmail) {
    public static AppConfig fromEnvironment() {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
        String learner = System.getenv().getOrDefault("LEARNER_EMAIL", "learner@example.com");
        String educator = System.getenv().getOrDefault("EDUCATOR_EMAIL", "educator@example.com");
        return new AppConfig(key, learner, educator);
    }
}

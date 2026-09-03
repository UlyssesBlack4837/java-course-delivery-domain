package com.example.learning;

import java.time.LocalDate;

public final class Main {
    public static void main(String[] args) throws Exception {
        String domain = args.length == 0 ? "example.edu" : args[0];
        AppConfig config = AppConfig.fromEnvironment();
        InfraiEmailClient infrai = new InfraiEmailClient(config.apiKey());
        System.out.println("domain verification: " + infrai.verifyDomain(domain));
        CourseDeliveryService service = new CourseDeliveryService();
        var messages = service.plan("Algebra I", config.learnerEmail(), config.educatorEmail(), LocalDate.now().plusDays(6), LocalDate.now());
        for (var message : messages) System.out.println("sent: " + infrai.send(message.recipient(), message.subject(), message.text()));
    }
}

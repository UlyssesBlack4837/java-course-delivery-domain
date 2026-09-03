package com.example.learning;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class CourseDeliveryService {
    public record PlannedMessage(String recipient, String subject, String text) {}
    public List<PlannedMessage> plan(String course, String learner, String educator, LocalDate deadline, LocalDate today) {
        long days = ChronoUnit.DAYS.between(today, deadline);
        if (days < 0 || days > 7) return List.of();
        List<PlannedMessage> messages = new ArrayList<>();
        messages.add(new PlannedMessage(learner, course + " deadline in " + days + " days", "Please finish your course work by " + deadline + "."));
        messages.add(new PlannedMessage(educator, course + " learner deadline report", "Learner deadline: " + deadline + "; reminder planned for " + learner + "."));
        return messages;
    }
}

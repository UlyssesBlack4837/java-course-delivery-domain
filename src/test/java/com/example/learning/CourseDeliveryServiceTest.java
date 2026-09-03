package com.example.learning;

import java.time.LocalDate;

public final class CourseDeliveryServiceTest {
    public static void main(String[] args) {
        var service = new CourseDeliveryService();
        var today = LocalDate.of(2026, 9, 1);
        if (service.plan("Biology", "learner@school.edu", "teacher@school.edu", today.plusDays(6), today).size() != 2) throw new AssertionError("six-day deadline should notify both roles");
        if (!service.plan("Biology", "learner@school.edu", "teacher@school.edu", today.plusDays(10), today).isEmpty()) throw new AssertionError("distant deadline should stay quiet");
        System.out.println("course deadline decision passed");
    }
}

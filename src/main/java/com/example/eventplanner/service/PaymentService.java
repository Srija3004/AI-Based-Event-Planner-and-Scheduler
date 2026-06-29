package com.example.eventplanner.service;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.Task;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.PaymentRepository;
import com.example.eventplanner.repository.TaskRepository;
import com.razorpay.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;

    public PaymentService(PaymentRepository paymentRepository, EventRepository eventRepository, TaskRepository taskRepository) {
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
    }

    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            String generated = Utils.getHash(orderId + "|" + paymentId, razorpaySecret);
            return generated.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void confirmEvent(Long eventId) {
        eventRepository.confirmPayment(eventId);

        // Automatically generate timeline tasks after payment confirmation
        generateDefaultTasks(eventId);
    }

    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    /** ----------------- TASK GENERATION LOGIC ----------------- **/
    public void generateDefaultTasks(Long eventId) {

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return;

        LocalDate eventDate;
        try {
            eventDate = LocalDate.parse(event.getDate()); // Convert from String
        } catch (Exception e) {
            eventDate = LocalDate.now();
        }

        Task t1 = new Task();
        t1.setTitle("Book Venue");
        t1.setDescription("Confirm and book the event venue.");
        t1.setStatus("pending");
        t1.setDate(eventDate.minusDays(20));
        t1.setEvent(event);

        Task t2 = new Task();
        t2.setTitle("Catering");
        t2.setDescription("Finalize caterers and menu.");
        t2.setStatus("pending");
        t2.setDate(eventDate.minusDays(15));
        t2.setEvent(event);

        Task t3 = new Task();
        t3.setTitle("Send Invitations");
        t3.setDescription("Send invites to guests.");
        t3.setStatus("pending");
        t3.setDate(eventDate.minusDays(10));
        t3.setEvent(event);

        Task t4 = new Task();
        t4.setTitle("Decoration Setup");
        t4.setDescription("Finalize decoration theme.");
        t4.setStatus("pending");
        t4.setDate(eventDate.minusDays(5));
        t4.setEvent(event);

        Task t5 = new Task();
        t5.setTitle("Photography");
        t5.setDescription("Confirm photography and videography.");
        t5.setStatus("pending");
        t5.setDate(eventDate.minusDays(3));
        t5.setEvent(event);

        Task t6 = new Task();
        t6.setTitle("Final Check");
        t6.setDescription("Review everything one day before.");
        t6.setStatus("pending");
        t6.setDate(eventDate.minusDays(1));
        t6.setEvent(event);

        taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6));
    }
}

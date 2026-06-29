package com.example.eventplanner.controller;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.Payment;
import com.example.eventplanner.model.Task;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.PaymentRepository;
import com.example.eventplanner.repository.TaskRepository;
import com.example.eventplanner.service.PaymentService;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/payment")
@CrossOrigin(origins = {"http://localhost:63342","http://127.0.0.1:5500"})
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;

    public PaymentController(PaymentRepository paymentRepository, PaymentService paymentService, TaskRepository taskRepository, EventRepository eventRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            int amount = Integer.parseInt(data.get("amount").toString());
            Long eventId = Long.valueOf(data.get("eventId").toString());

            Event event = paymentService.getEvent(eventId);
            if (event == null)
                return ResponseEntity.badRequest().body(Map.of("error", "Event not found"));

            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject options = new JSONObject();
            options.put("amount", amount);
            options.put("currency", "INR");
            options.put("receipt", "event_" + eventId);

            com.razorpay.Order razorOrder = client.orders.create(options);

            Payment p = new Payment();
            p.setEventId(eventId);
            p.setAmount(amount / 100.0);
            p.setRazorpayOrderId(razorOrder.get("id"));
            p.setStatus("PENDING");
            p.setPlanner(event.getPlanner());

            paymentRepository.save(p);

            return ResponseEntity.ok(Map.of(
                    "id", razorOrder.get("id"),
                    "amount", amount,
                    "currency", "INR",
                    "key", razorpayKey
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> data) {
        try {
            String orderId = data.get("razorpay_order_id").toString();
            String paymentId = data.get("razorpay_payment_id").toString();
            String signature = data.get("razorpay_signature").toString();

            Long eventId = Long.valueOf(data.get("eventId").toString());

            if (!paymentService.verifyPayment(orderId, paymentId, signature)) {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Invalid Signature"));
            }

            Payment p = paymentRepository.findByRazorpayOrderId(orderId);
            if (p != null) {
                p.setRazorpayPaymentId(paymentId);
                p.setRazorpaySignature(signature);
                p.setStatus("PAID");
                p.setPaymentDate(java.time.LocalDateTime.now());
                paymentRepository.save(p);
            }

            paymentService.confirmEvent(eventId);
            generateDefaultTasks(eventId);

            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Payment Verified & Tasks Created"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    private void generateDefaultTasks(Long eventId) {

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return;

        // ---- FIX: Handle multiple date formats ----
        LocalDate eventDate;
        try {
            Object rawDate = event.getDate();

            if (rawDate instanceof LocalDate) {
                eventDate = (LocalDate) rawDate;
            } else if (rawDate instanceof LocalDateTime) {
                eventDate = ((LocalDateTime) rawDate).toLocalDate();
            } else {
                // Raw value is String (most likely your case)
                eventDate = LocalDate.parse(rawDate.toString());
            }

        } catch (Exception e) {
            eventDate = LocalDate.now(); // fallback to today if invalid
        }

        // ---- Create tasks ----
        Task t1 = new Task();
        t1.setTitle("Book Venue");
        t1.setDescription("Confirm hall booking and vendor contract.");
        t1.setStatus("pending");
        t1.setDate(eventDate.minusDays(20));
        t1.setEvent(event);

        Task t2 = new Task();
        t2.setTitle("Catering Setup");
        t2.setDescription("Finalize caterers and food menu.");
        t2.setStatus("pending");
        t2.setDate(eventDate.minusDays(15));
        t2.setEvent(event);

        Task t3 = new Task();
        t3.setTitle("Send Invitations");
        t3.setDescription("Share invitations with guests.");
        t3.setStatus("pending");
        t3.setDate(eventDate.minusDays(10));
        t3.setEvent(event);

        Task t4 = new Task();
        t4.setTitle("Decoration");
        t4.setDescription("Confirm decoration theme.");
        t4.setStatus("pending");
        t4.setDate(eventDate.minusDays(5));
        t4.setEvent(event);

        Task t5 = new Task();
        t5.setTitle("Photography & Media");
        t5.setDescription("Confirm photographer & videographer.");
        t5.setStatus("pending");
        t5.setDate(eventDate.minusDays(3));
        t5.setEvent(event);

        Task t6 = new Task();
        t6.setTitle("Final Event Check");
        t6.setDescription("Cross-check everything before event day.");
        t6.setStatus("pending");
        t6.setDate(eventDate.minusDays(1));
        t6.setEvent(event);

        taskRepository.saveAll(List.of(t1, t2, t3, t4, t5, t6));
    }
    @GetMapping("/earnings/{plannerId}")
    public ResponseEntity<?> getEarnings(@PathVariable Long plannerId) {

        double totalEarnings = paymentRepository.findByPlanner_IdAndStatus(plannerId, "PAID")
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double monthlyEarnings = paymentRepository.findByPlanner_IdAndStatus(plannerId, "PAID")
                .stream()
                .filter(p -> p.getPaymentDate() != null && p.getPaymentDate().getMonthValue() == LocalDate.now().getMonthValue())
                .mapToDouble(Payment::getAmount)
                .sum();

        List<Payment> transactions = paymentRepository.findByPlanner_IdAndStatus(plannerId, "PAID");

        return ResponseEntity.ok(Map.of(
                "totalEarnings", totalEarnings,
                "monthlyEarnings", monthlyEarnings,
                "pendingAmount", 0,   // optional if needed later
                "transactions", transactions
        ));
    }

}
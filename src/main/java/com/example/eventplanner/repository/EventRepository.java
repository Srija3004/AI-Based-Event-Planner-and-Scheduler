package com.example.eventplanner.repository;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUser(User user);
    List<Event> findByPlanner_Id(Long plannerId);  // FIXED
    List<Event> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.paymentStatus = :status, e.razorpayOrderId = :orderId WHERE e.id = :eventId")
    void updatePaymentStatus(Long eventId, String status, String orderId);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.status = 'confirmed', e.paymentStatus = 'PAID', e.paymentDate = CURRENT_TIMESTAMP WHERE e.id = :eventId")
    void confirmPayment(Long eventId);

    Event findTopByUserIdOrderByIdDesc(Long userId);
}

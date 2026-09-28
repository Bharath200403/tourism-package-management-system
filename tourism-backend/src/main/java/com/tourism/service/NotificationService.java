package com.tourism.service;

import com.tourism.entity.Notification;
import com.tourism.entity.User;
import com.tourism.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void notify(User user, String title, String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        notificationRepository.save(n);
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markRead(Long notificationId, Long userId) {
        Notification n = notificationRepository.findById(notificationId)
                .filter(x -> x.getUser().getId().equals(userId))
                .orElseThrow(() -> new com.tourism.exception.ResourceNotFoundException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }
}

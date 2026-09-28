package com.tourism.service;

import com.tourism.dto.request.FeedbackRequest;
import com.tourism.dto.response.FeedbackResponseDTO;
import com.tourism.entity.Feedback;
import com.tourism.entity.User;
import com.tourism.mapper.DtoMapper;
import com.tourism.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public FeedbackResponseDTO submit(User customer, FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setSubject(request.getSubject());
        feedback.setMessage(request.getMessage());
        feedbackRepository.save(feedback);
        return DtoMapper.toFeedbackDTO(feedback);
    }

    public List<FeedbackResponseDTO> all() {
        return feedbackRepository.findAll().stream().map(DtoMapper::toFeedbackDTO).toList();
    }

    public List<FeedbackResponseDTO> mine(Long customerId) {
        return feedbackRepository.findByCustomerId(customerId).stream().map(DtoMapper::toFeedbackDTO).toList();
    }
}

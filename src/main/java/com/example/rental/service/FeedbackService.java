package com.example.rental.service;

import com.example.rental.dto.FeedbackDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    FeedbackDto createFeedback(FeedbackDto feedbackDto);
    FeedbackDto getFeedbackById(Long id);
    FeedbackDto updateFeedback(Long id, FeedbackDto feedbackDto);
    void deleteFeedback(Long id);

    Page<FeedbackDto> getAllFeedback(Pageable pageable);
}

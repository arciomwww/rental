package com.example.rental.service;

import com.example.rental.dto.FeedbackDto;
import com.example.rental.entity.Feedback;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.FeedbackRepository;
import lombok.extern.slf4j.Slf4j;
import com.example.rental.entity.User;
import com.example.rental.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public FeedbackDto createFeedback(FeedbackDto feedbackDto) {
        if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setUser(new User(feedbackDto.getUserId()));
        feedback.setCar(new Car(feedbackDto.getCarId()));
        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Created feedback: {}", savedFeedback);
        return mapToDto(savedFeedback);
    }

    public Page<FeedbackDto> getAllFeedback(Pageable pageable) {
        Page<Feedback> feedbackPage = feedbackRepository.findAll(pageable);
        log.info("Retrieved all feedback with pagination");
        return feedbackPage.map(this::mapToDto);
    }

    public FeedbackDto getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        log.info("Retrieved feedback: {}", feedback);
        return mapToDto(feedback);
    }
    @Transactional
    public FeedbackDto updateFeedback(Long id, FeedbackDto feedbackDto) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Feedback not found with id {}", id);
                    return new ResourceNotFoundException("Feedback not found with id " + id);
                });

        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Updated feedback with ID: {}, Comment: {}, Rating: {}", feedback.getId(), feedback.getComment(), feedback.getRating());
        return mapToDto(updatedFeedback);
    }
    @Transactional
    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        feedbackRepository.delete(feedback);
        log.info("Deleted feedback: Успех");

    }

    private FeedbackDto mapToDto(Feedback feedback) {
        return FeedbackDto.builder()
                .id(feedback.getId())
                .userId(feedback.getUser().getId())
                .carId(feedback.getCar().getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}

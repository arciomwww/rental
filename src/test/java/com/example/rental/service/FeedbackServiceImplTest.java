package com.example.rental.service;

import com.example.rental.dto.FeedbackDto;
import com.example.rental.entity.Feedback;
import com.example.rental.entity.User;
import com.example.rental.entity.Car;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Feedback feedback;
    private FeedbackDto feedbackDto;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setUser(new User(1L));
        feedback.setCar(new Car(1L));
        feedback.setRating(4);
        feedback.setComment("Great service!");
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackDto = FeedbackDto.builder()
                .id(1L)
                .userId(1L)
                .carId(1L)
                .rating(4)
                .comment("Great service!")
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    @Test
    void createFeedback_ShouldReturnCreatedFeedbackDto() {
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackDto result = feedbackService.createFeedback(feedbackDto);

        assertEquals(feedbackDto.getRating(), result.getRating());
        assertEquals(feedbackDto.getComment(), result.getComment());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void createFeedback_InvalidRating_ShouldThrowException() {
        feedbackDto.setRating(6);  // Invalid rating

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                feedbackService.createFeedback(feedbackDto));

        assertEquals("Rating must be between 1 and 5", exception.getMessage());
    }

    @Test
    void getAllFeedback_ShouldReturnPageOfFeedbackDto() {
        Page<Feedback> feedbackPage = new PageImpl<>(Collections.singletonList(feedback));
        when(feedbackRepository.findAll(any(PageRequest.class))).thenReturn(feedbackPage);

        Page<FeedbackDto> result = feedbackService.getAllFeedback(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(feedback.getComment(), result.getContent().get(0).getComment());
    }

    @Test
    void getFeedbackById_ExistingId_ShouldReturnFeedbackDto() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        FeedbackDto result = feedbackService.getFeedbackById(1L);

        assertEquals(feedbackDto.getComment(), result.getComment());
        assertEquals(feedbackDto.getRating(), result.getRating());
    }

    @Test
    void getFeedbackById_NonExistingId_ShouldThrowResourceNotFoundException() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                feedbackService.getFeedbackById(1L));

        assertEquals("Feedback not found with id: 1", exception.getMessage());
    }

    @Test
    void updateFeedback_ShouldReturnUpdatedFeedbackDto() {
        feedbackDto.setComment("Updated comment");
        feedbackDto.setRating(5);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);

        FeedbackDto result = feedbackService.updateFeedback(1L, feedbackDto);

        assertEquals(feedbackDto.getComment(), result.getComment());
        assertEquals(feedbackDto.getRating(), result.getRating());
        verify(feedbackRepository, times(1)).save(feedback);
    }

    @Test
    void deleteFeedback_ShouldDeleteFeedback() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        feedbackService.deleteFeedback(1L);

        verify(feedbackRepository, times(1)).delete(feedback);
    }

    @Test
    void deleteFeedback_NonExistingId_ShouldThrowResourceNotFoundException() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                feedbackService.deleteFeedback(1L));

        assertEquals("Feedback not found with id: 1", exception.getMessage());
    }
}

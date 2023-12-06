package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.BookingRequestRepository;
import com.sspdev.hotelbooking.dto.BookingRequestCreateEditDto;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import com.sspdev.hotelbooking.mapper.BookingRequestCreateEditMapper;
import com.sspdev.hotelbooking.mapper.BookingRequestReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingRequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRequestReadMapper bookingRequestReadMapper;
    private final BookingRequestCreateEditMapper bookingRequestCreteEditMapper;

    public Optional<BookingRequestReadDto> findById(Long id) {
        return bookingRequestRepository.findById(id)
                .map(bookingRequestReadMapper::map);
    }

    @Transactional
    public BookingRequestReadDto create(BookingRequestCreateEditDto createDto) {
        return Optional.of(createDto)
                .map(bookingRequestCreteEditMapper::map)
                .map(bookingRequestRepository::save)
                .map(bookingRequestReadMapper::map)
                .orElseThrow();
    }

    public long getTotalRequests() {
        return bookingRequestRepository.count();
    }

    public Map<String, Long> countRequestsByStatus() {
        return bookingRequestRepository.findAll().stream()
                .collect(groupingBy(request -> request.getStatus().name(), counting()));
    }

    public Map<String, Long> countRequestsByUserAndStatus(Integer id) {
        return bookingRequestRepository.findByUser(id).stream()
                .collect(groupingBy(request -> request.getStatus().name(), counting()));
    }

    public Map<String, Long> countRequestsByOwnerAndStatus(Integer id) {
        return bookingRequestRepository.countRequestsByOwnerAndStatus(id).stream()
                .collect(groupingBy(request -> request.getStatus().name(), counting()));
    }
}
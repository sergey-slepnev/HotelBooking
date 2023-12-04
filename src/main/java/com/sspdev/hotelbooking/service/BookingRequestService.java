package com.sspdev.hotelbooking.service;

import com.sspdev.hotelbooking.database.repository.BookingRequestRepository;
import com.sspdev.hotelbooking.dto.BookingRequestCreateEditDto;
import com.sspdev.hotelbooking.dto.BookingRequestReadDto;
import com.sspdev.hotelbooking.mapper.BookingRequestCreateEditMapper;
import com.sspdev.hotelbooking.mapper.BookingRequestReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}

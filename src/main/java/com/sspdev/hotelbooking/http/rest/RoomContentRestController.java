package com.sspdev.hotelbooking.http.rest;

import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.RoomContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms")
@ControllerAdvice
public class RoomContentRestController {

    private static final String ERROR_MESSAGE_KEY = "error.invalid_content_size";
    private static final Integer BYTES_IN_KILOBYTE = 1024;

    private final RoomContentService roomContentService;
    private final ApplicationContentService applicationContentService;
    private final Set<String> errors;
    private final MessageSource messageSource;
    @Value("${spring.servlet.multipart.max-request-size}")
    private final String defaultMaxFileSizeToUpload;

    @GetMapping(value = "/{roomId}/content/{contentId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("roomId") Integer roomId,
                                           @PathVariable("contentId") Integer contentId) {
        return roomContentService.findByRoom(roomId).stream()
                .filter(content -> content.getId().equals(contentId))
                .findFirst()
                .map(RoomContentReadDto::getLink)
                .filter(StringUtils::hasText)
                .map(imageName -> applicationContentService.getImage(imageName).get())
                .map(content -> ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentLength(content.length)
                        .body(content))
                .orElseGet(notFound()::build);
    }

    @PostMapping("/content/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id,
                                    @SessionAttribute("room") RoomReadDto room) {
        return roomContentService.delete(id)
                ? status(HttpStatus.FOUND)
                .location(URI.create("/my-booking/rooms/" + room.getId()))
                .build()
                : notFound().build();
    }

    @PostMapping("/{roomId}/content/create")
    public ResponseEntity<RoomContentReadDto> create(@PathVariable("roomId") Integer roomId,
                                                     @ModelAttribute("content") @Validated RoomContentCreateDto contentCreateDto,
                                                     BindingResult bindingResult,
                                                     RedirectAttributes redirectAttributes) {
        ResponseEntity<RoomContentReadDto> responseWithoutContent = status(HttpStatus.FOUND)
                .location(URI.create("/my-booking/rooms/" + roomId))
                .build();
        if (!contentCreateDto.getContent().isEmpty()) {
            if (bindingResult.hasErrors()) {
                var creationErrors = bindingResult.getAllErrors().stream().
                        map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                errors.addAll(creationErrors);
                redirectAttributes.addFlashAttribute("errors", errors);
                return responseWithoutContent;
            }
            contentCreateDto.setRoomId(roomId);
            roomContentService.save(contentCreateDto);
            return status(HttpStatus.FOUND)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(contentCreateDto.getContent().getSize())
                    .location(URI.create("/my-booking/rooms/" + roomId))
                    .build();
        } else {
            return responseWithoutContent;
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RoomContentReadDto> handlerException(MaxUploadSizeExceededException exception,
                                                               @SessionAttribute("room") RoomReadDto room,
                                                               RedirectAttributes redirectAttributes,
                                                               Locale locale) {
        var exceptionMessage = exception.getCause().getMessage();
        var contentSizeInMB = Double.parseDouble(exceptionMessage
                .substring(exceptionMessage.indexOf("(") + 1, exceptionMessage.indexOf(")")))
                / (BYTES_IN_KILOBYTE * BYTES_IN_KILOBYTE);
        var roundedContentSizeInMB = String.format("%.3f", contentSizeInMB);

        Object[] args = {defaultMaxFileSizeToUpload, roundedContentSizeInMB};
        var message = messageSource.getMessage(ERROR_MESSAGE_KEY, args, locale);
        errors.add(message);
        redirectAttributes.addFlashAttribute("errors", errors);
        return status(HttpStatus.FOUND)
                .location(URI.create("/my-booking/rooms/" + room.getId()))
                .build();
    }
}
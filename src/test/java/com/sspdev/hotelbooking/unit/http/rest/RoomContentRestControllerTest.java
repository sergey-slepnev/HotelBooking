package com.sspdev.hotelbooking.unit.http.rest;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import com.sspdev.hotelbooking.dto.RoomContentCreateDto;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import com.sspdev.hotelbooking.dto.RoomReadDto;
import com.sspdev.hotelbooking.http.rest.RoomContentRestController;
import com.sspdev.hotelbooking.service.ApplicationContentService;
import com.sspdev.hotelbooking.service.RoomContentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sspdev.hotelbooking.dto.RoomContentCreateDto.Fields.roomId;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@WebMvcTest(controllers = RoomContentRestController.class)
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
class RoomContentRestControllerTest {

    private static final Integer EXISTENT_ROOM_ID = 1;
    private static final Integer EXISTENT_CONTENT_ID = 1;
    private static final Integer NON_EXISTENT_CONTENT_ID = 999;
    private static final Integer EXISTENT_HOTEL_ID = 1;

    @MockBean
    private final RoomContentService roomContentService;

    @MockBean
    private final ApplicationContentService applicationContentService;

    private final MockMvc mockMvc;

    @Test
    void getImage_shouldReturnImageByRoom_whenImageExists() throws Exception {
        var existentContent = getRoomContentReadDto();
        when(roomContentService.findByRoom(EXISTENT_ROOM_ID)).thenReturn(List.of(existentContent));
        when(applicationContentService.getImage(existentContent.getLink())).thenReturn(Optional.of(new byte[]{0, 0, 0}));

        mockMvc.perform(get("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/" + EXISTENT_CONTENT_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM));
    }

    @Test
    void getImage_shouldReturnNotFound_whenImageNotExists() throws Exception {
        when(roomContentService.findByRoom(EXISTENT_ROOM_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/" + NON_EXISTENT_CONTENT_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldDeleteContentAndRedirectToRoomPage_whenContentExists() throws Exception {
        var roomInSession = getRoomReadDto();
        when(roomContentService.delete(EXISTENT_CONTENT_ID)).thenReturn(true);

        mockMvc.perform(post("/api/v1/rooms/content/" + EXISTENT_CONTENT_ID + "/delete")
                        .sessionAttr("room", roomInSession)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{d\\+}"));
    }

    @Test
    void delete_shouldReturnNotFound_whenContentNotExist() throws Exception {
        var roomInSession = getRoomReadDto();
        when(roomContentService.delete(EXISTENT_CONTENT_ID)).thenReturn(true);

        mockMvc.perform(post("/api/v1/rooms/content/" + NON_EXISTENT_CONTENT_ID + "/delete")
                        .sessionAttr("room", roomInSession)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Disabled("Unexpected exception during isValid call")
    void create_shouldCreateNewRoomContent_whenContentExists() throws Exception {
        var createDto = getRoomContentCreateDto();
        var readDto = getRoomContentReadDto();

        when(roomContentService.save(createDto)).thenReturn(readDto);

        mockMvc.perform(multipart("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/create")
                        .file("content", createDto.getContent().getBytes())
                        .param(roomId, String.valueOf(EXISTENT_ROOM_ID))
                        .with(csrf()))
                .andDo(print())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{d\\+}"));
    }

    @Test
    @Disabled("Unexpected exception during isValid call")
    void create_shouldJustRedirectToRoomPage_whenContentEmpty() throws Exception {
        var createDto = getRoomContentCreateDto();
        var readDto = getRoomContentReadDto();

        when(roomContentService.save(createDto)).thenReturn(readDto);

        mockMvc.perform(multipart("/api/v1/rooms/" + EXISTENT_ROOM_ID + "/content/create")
                        .file("content", createDto.getContent().getBytes())
                        .param(roomId, String.valueOf(EXISTENT_ROOM_ID))
                        .with(csrf()))
                .andDo(print())
                .andExpect(content().contentType(APPLICATION_OCTET_STREAM))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/rooms/{d\\+}"));
    }

    private RoomContentReadDto getRoomContentReadDto() {
        return new RoomContentReadDto(
                EXISTENT_CONTENT_ID,
                "testPhoto.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_ROOM_ID
        );
    }

    private RoomReadDto getRoomReadDto() {
        return new RoomReadDto(
                EXISTENT_ROOM_ID,
                EXISTENT_HOTEL_ID,
                1,
                RoomType.DBL,
                44.4,
                3,
                0,
                BigDecimal.valueOf(1900),
                1,
                true,
                "Отличный отель",
                null
        );
    }

    private RoomContentCreateDto getRoomContentCreateDto() {
        var content = new MockMultipartFile(
                "content",
                "testPhoto.jpg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[]{0, 0, 0, 0});
        return new RoomContentCreateDto(
                content,
                ContentType.PHOTO,
                EXISTENT_ROOM_ID
        );
    }
}
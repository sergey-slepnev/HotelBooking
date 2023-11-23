package com.sspdev.hotelbooking.unit.http.controller;

import com.sspdev.hotelbooking.database.entity.enums.ContentType;
import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Star;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import com.sspdev.hotelbooking.dto.HotelContentCreateDto;
import com.sspdev.hotelbooking.dto.HotelContentReadDto;
import com.sspdev.hotelbooking.dto.HotelCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto;
import com.sspdev.hotelbooking.dto.HotelDetailsReadDto;
import com.sspdev.hotelbooking.dto.HotelReadDto;
import com.sspdev.hotelbooking.dto.PageResponse;
import com.sspdev.hotelbooking.dto.UserReadDto;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import com.sspdev.hotelbooking.http.controller.HotelController;
import com.sspdev.hotelbooking.service.HotelContentService;
import com.sspdev.hotelbooking.service.HotelDetailsService;
import com.sspdev.hotelbooking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sspdev.hotelbooking.dto.HotelCreateEditDto.Fields.name;
import static com.sspdev.hotelbooking.dto.HotelCreateEditDto.Fields.ownerId;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.area;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.description;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.floorCount;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.hotelId;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.locality;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.phoneNumber;
import static com.sspdev.hotelbooking.dto.HotelDetailsCreateEditDto.Fields.street;
import static com.sspdev.hotelbooking.dto.filter.HotelFilter.Fields.country;
import static com.sspdev.hotelbooking.dto.filter.HotelFilter.Fields.star;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {

    private static final Integer EXISTENT_HOTEL_ID = 1;
    private static final Integer NON_EXISTENT_HOTEL_ID = 999;
    private static final Integer EXISTENT_HOTEL_CONTENT_ID = 1;
    private static final Integer EXISTENT_OWNER_ID = 4;
    private static final Integer EXISTENT_HOTEL_DETAILS_ID = 1;

    private MockMvc mockMvc;
    @Mock
    private HotelService hotelService;
    @Mock
    private HotelDetailsService hotelDetailsService;
    @Mock
    private HotelContentService hotelContentService;
    @Mock
    private Page<HotelReadDto> hotelReadDtoPage;
    @InjectMocks
    private HotelController hotelController;

    @BeforeEach
    void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setHandlerExceptionResolvers()
                .alwaysDo(print())
                .build();
    }

    @Test
    void findById_shouldReturnPageWithHotelAndHotelDetailsAndContent_whenExist() throws Exception {
        var hotel = getHotelReadDto();
        var hotelDetails = getHotelDetailsReadDto();
        var contents = getHotelContentReadDto();

        when(hotelService.findById(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotel));
        when(hotelDetailsService.findByHotelId(EXISTENT_HOTEL_ID)).thenReturn(Optional.of(hotelDetails));
        when(hotelContentService.findContent(EXISTENT_HOTEL_ID)).thenReturn(List.of(contents));

        mockMvc.perform(get("/my-booking/hotels/" + EXISTENT_HOTEL_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotel", "hotelDetails", "contents"))
                .andExpect(model().size(3))
                .andExpect(model().attribute("contents", hasSize(1)))
                .andExpect(view().name("hotel/hotel"));
    }

    @Test
    void findById_shouldReturnNotFound_whenHotelNotExist() throws Exception {
        when(hotelService.findById(NON_EXISTENT_HOTEL_ID)).thenReturn(Optional.empty());
        when(hotelDetailsService.findByHotelId(NON_EXISTENT_HOTEL_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/my-booking/hotels/" + NON_EXISTENT_HOTEL_ID))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 5L, 20L, 100L})
    void findAll_shouldFindAllRooms_whenNoPredicatesFilter(long expectedTotalElements) throws Exception {
        var pageMetadata = new PageResponse.Metadata(0, 20, expectedTotalElements);
        var expectedResponseContent = List.of(getHotelReadDto());
        var pageResponse = new PageResponse<>(expectedResponseContent, pageMetadata);

        when(hotelReadDtoPage.getContent()).thenReturn(expectedResponseContent);
        when(hotelReadDtoPage.getSize()).thenReturn(20);
        when(hotelReadDtoPage.getTotalElements()).thenReturn(expectedTotalElements);
        when(hotelService.findAllByFilter(any(HotelFilter.class), any(Pageable.class))).thenReturn(hotelReadDtoPage);

        var mvcResult = mockMvc.perform(get("/my-booking/hotels"))
                .andExpectAll(
                        status().isOk(),
                        model().attributeExists("hotels", "filter"),
                        model().attribute("hotels", pageResponse)
                ).andReturn();

        var hotelsToString = mvcResult.getModelAndView().getModel().get("hotels").toString();

        assertThat(hotelsToString).contains("totalElements=" + expectedTotalElements);
    }

    @Test
    void add_shouldReturnAddHotelPage() throws Exception {
        var userInSession = getUserReadDto();
        var hotelDetails = getHotelDetailsCreatedEditDto();
        mockMvc.perform(get("/my-booking/hotels/" + EXISTENT_OWNER_ID + "/add-hotel")
                        .sessionAttr("user", userInSession)
                        .sessionAttr("hotelDetails", hotelDetails))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotelCreateDto", "hotelDetails", "hotelContent", "stars"))
                .andExpect(view().name("hotel/add"));
    }

    @Test
    @Disabled("Unable to instantiate ConstraintValidator")
    void create_shouldRedirectToAddHotelPage_whenDtoInvalid() throws Exception {
        var userInSession = getHotelReadDto();
        var createHotelDto = getHotelCreateEditDto();
        var hotelDetailsCreatedDto = getHotelDetailsCreatedEditDto();
        var hotelContentCreateDto = getHotelContentCreateDto();
        var hotelDetails = getHotelDetailsCreatedEditDto();

        when(hotelService.create(createHotelDto, hotelDetailsCreatedDto, hotelContentCreateDto)).thenReturn(getHotelReadDto());

        mockMvc.perform(post("/my-booking/hotels/" + EXISTENT_OWNER_ID + "/create")
                        .sessionAttr("user", userInSession)
                        .sessionAttr("hotelDetails", hotelDetails)
                        .param(ownerId, String.valueOf(EXISTENT_OWNER_ID))
                        .param(name, "T")
                        .param(hotelId, String.valueOf(EXISTENT_HOTEL_ID))
                        .param(phoneNumber, "0")
                        .param(country, "")
                        .param(locality, "")
                        .param(area, "0")
                        .param(street, "")
                        .param(floorCount, "five")
                        .param(star, Star.FIVE.name())
                        .param(description, "Замечательный отель"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(6))
                .andExpect(redirectedUrlPattern("/my-booking/hotels/{d\\+}/add-hotel"));
    }

    @Test
    void delete_shouldDeleteHotelAndRedirectToOwnerPage_ifHotelExists() throws Exception {
        var ownerInSession = getUserReadDto();

        when(hotelService.delete(EXISTENT_HOTEL_ID)).thenReturn(true);

        mockMvc.perform(post("/my-booking/hotels/" + EXISTENT_HOTEL_ID + "/delete")
                        .sessionAttr("user", ownerInSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/my-booking/users/{d\\+}"));
        var captor = ArgumentCaptor.forClass(Integer.class);
        verify(hotelService).delete(captor.capture());
    }

    @Test
    void delete_shouldReturnNotFound_ifHotelNotExist() throws Exception {
        var ownerInSession = getUserReadDto();

        when(hotelService.delete(NON_EXISTENT_HOTEL_ID)).thenReturn(false);

        mockMvc.perform(post("/my-booking/hotels/" + NON_EXISTENT_HOTEL_ID + "/delete")
                        .sessionAttr("user", ownerInSession))
                .andExpect(status().isNotFound());
        verify(hotelService).delete(NON_EXISTENT_HOTEL_ID);
    }

    private HotelReadDto getHotelReadDto() {
        return new HotelReadDto(
                EXISTENT_HOTEL_ID,
                EXISTENT_OWNER_ID,
                "Hotel",
                true,
                Status.APPROVED
        );
    }

    private HotelDetailsReadDto getHotelDetailsReadDto() {
        return new HotelDetailsReadDto(
                EXISTENT_HOTEL_DETAILS_ID,
                EXISTENT_HOTEL_ID,
                "8-800-300-400",
                "Россия",
                "Самара",
                "Южный район",
                "Новая",
                4,
                Star.ONE,
                "Лучший отель!"
        );
    }

    private HotelContentReadDto getHotelContentReadDto() {
        return new HotelContentReadDto(
                EXISTENT_HOTEL_CONTENT_ID,
                "test.jpg",
                ContentType.PHOTO.name(),
                EXISTENT_HOTEL_ID
        );
    }

    private UserReadDto getUserReadDto() {
        return new UserReadDto(
                EXISTENT_OWNER_ID,
                Role.USER,
                "user",
                "123",
                "Petr",
                "Petrov",
                LocalDate.of(2000, 10, 10),
                "+7-954-984-98-98",
                "user_avatar",
                Status.NEW,
                LocalDateTime.of(2023, 5, 5, 10, 10));
    }

    private HotelCreateEditDto getHotelCreateEditDto() {
        return new HotelCreateEditDto(
                EXISTENT_OWNER_ID,
                "TestHotel",
                true,
                Status.APPROVED
        );
    }

    private HotelDetailsCreateEditDto getHotelDetailsCreatedEditDto() {
        return new HotelDetailsCreateEditDto(
                EXISTENT_HOTEL_ID,
                "+7-965-78-78-888",
                "Россия",
                "Москва",
                "Центральный",
                "Новокузнецкая",
                3,
                Star.FIVE,
                "Очень хороший отель"
        );
    }

    private HotelContentCreateDto getHotelContentCreateDto() {
        return new HotelContentCreateDto(
                new MockMultipartFile("test.jpg", "test.jpg".getBytes()),
                ContentType.PHOTO,
                EXISTENT_HOTEL_ID
        );
    }
}
package com.sspdev.hotelbooking.database.entity;

import com.sspdev.hotelbooking.database.entity.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@NamedEntityGraph(
        name = "Hotel.hotelContents",
        attributeNodes = {
                @NamedAttributeNode("hotelContents")
        })
@NamedEntityGraph(
        name = "Hotel.hotelDetails",
        attributeNodes = {
                @NamedAttributeNode("hotelDetails")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"owner", "name"})
@ToString(exclude = {"owner", "rooms", "reviews"})
@Entity
public class Hotel implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;


    private String name;

    private boolean available;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder.Default
    @OneToMany(mappedBy = "hotel", cascade = ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "hotel", cascade = ALL, orphanRemoval = true)
    private List<BookingRequest> requests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "hotel", cascade = ALL, orphanRemoval = true)
    private List<HotelContent> hotelContents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "hotel", cascade = ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "hotel", cascade = ALL, orphanRemoval = true)
    private HotelDetails hotelDetails;

    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this);
    }

    public void addRequest(BookingRequest request) {
        requests.add(request);
        request.setHotel(this);
    }

    public void addContent(HotelContent hotelContent) {
        hotelContents.add(hotelContent);
        hotelContent.setHotel(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setHotel(this);
    }
}
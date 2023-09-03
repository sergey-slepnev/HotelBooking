package com.sspdev.hotelbooking.database.entity;

import com.sspdev.hotelbooking.database.entity.enums.Role;
import com.sspdev.hotelbooking.database.entity.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "username")
@ToString(exclude = {"id", "hotels", "requests", "reviews"})
@Entity
@Table(name = "users")
public class User implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String username;

    private String password;

    private PersonalInfo personalInfo;

    @Column(unique = true)
    private String phone;

    private String image;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime registeredAt;

    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = {PERSIST, MERGE, DETACH, REFRESH})
    private List<Hotel> hotels = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<BookingRequest> requests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = {PERSIST, MERGE})
    private List<Review> reviews = new ArrayList<>();

    public void addHotel(Hotel hotel) {
        hotels.add(hotel);
        hotel.setOwner(this);
    }

    public void addBookingRequest(BookingRequest request) {
        requests.add(request);
        request.setUser(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setUser(this);
    }
}
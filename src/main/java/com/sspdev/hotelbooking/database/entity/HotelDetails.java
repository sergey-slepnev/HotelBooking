package com.sspdev.hotelbooking.database.entity;

import com.sspdev.hotelbooking.database.entity.enums.Star;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"phoneNumber", "country", "locality"})
@ToString(exclude = {"id", "hotel"})
@Entity
public class HotelDetails implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    private Hotel hotel;

    private String phoneNumber;

    private String country;

    private String locality;

    private String area;

    private String street;

    private Integer floorCount;

    @Enumerated(EnumType.STRING)
    private Star star;

    private String description;

    public void setHotel(Hotel hotel) {
        hotel.setHotelDetails(this);
        this.hotel = hotel;
    }
}
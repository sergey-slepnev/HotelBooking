package com.sspdev.hotelbooking.database.entity;

import com.sspdev.hotelbooking.converter.RatingConverter;
import com.sspdev.hotelbooking.database.entity.enums.Rating;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"hotel", "user", "createdAt"})
@ToString(exclude = {"id", "reviewContents"})
@Entity
public class Review implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    @Convert(converter = RatingConverter.class)
    private Rating rating;

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = ALL, orphanRemoval = true)
    private List<ReviewContent> reviewContents = new ArrayList<>();

    public void addContent(ReviewContent content) {
        reviewContents.add(content);
        content.setReview(this);
    }
}
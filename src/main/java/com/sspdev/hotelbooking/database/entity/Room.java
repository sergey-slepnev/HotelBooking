package com.sspdev.hotelbooking.database.entity;

import com.sspdev.hotelbooking.converter.CostConverter;
import com.sspdev.hotelbooking.database.entity.enums.RoomType;
import jakarta.persistence.Convert;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
        name = "Room.roomContents",
        attributeNodes = {
                @NamedAttributeNode("roomContents")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "roomNo")
@ToString(exclude = {"id", "hotel", "requests", "roomContents"})
@Entity
public class Room implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private Integer roomNo;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    private Double square;

    private Integer adultBedCount;

    private Integer childrenBedCount;

    @Convert(converter = CostConverter.class)
    private BigDecimal cost;

    private boolean available;

    private Integer floor;

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "room")
    private List<BookingRequest> requests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "room")
    private List<RoomContent> roomContents = new ArrayList<>();

    public void addRequest(BookingRequest request) {
        requests.add(request);
        request.setRoom(this);
    }

    public void addContent(RoomContent content) {
        roomContents.add(content);
        content.setRoom(this);
    }
}
package com.sspdev.hotelbooking.database.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.sspdev.hotelbooking.database.entity.Room;
import com.sspdev.hotelbooking.database.querydsl.QPredicates;
import com.sspdev.hotelbooking.dto.HotelRoomInfo;
import com.sspdev.hotelbooking.dto.filter.RoomFilter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.sspdev.hotelbooking.database.entity.QRoom.room;

@RequiredArgsConstructor
public class FilterRoomRepositoryImpl implements FilterRoomRepository {

    private static final String SEARCH = """
            SELECT DISTINCT h.id                                         hotel_id,
                            h.name                                       hotel_name,
                            r.id                                         room_id,
                            FIRST_VALUE(hc.id) OVER (PARTITION BY h.id ) hotel_content_id
            FROM room r
                     JOIN hotel h ON h.id = r.hotel_id
                     LEFT JOIN hotel_content hc ON h.id = hc.hotel_id
                     LEFT JOIN booking_request br ON r.id = br.room_id
                     LEFT OUTER JOIN hotel_details hd ON h.id = hd.hotel_id
            WHERE (hc.type = 'PHOTO' OR hc.type IS NULL)
                AND r.available = TRUE
                AND br.id IS NULL
               OR (br.check_in NOT BETWEEN ? AND ?
                       AND br.check_out NOT BETWEEN ? AND ?
                OR br.status = 'CANCELED')
                AND br.check_in <= ?
                AND (hd.country = ?)
                AND (hd.locality = ?)
                AND (r.cost >= ? AND r.cost <= ?)
                AND (r.adult_bed_count = ?)
                AND r.children_bed_count = ?
            GROUP BY h.id,
                     h.name,
                     r.id,
                     hc.id
            """;

    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<HotelRoomInfo> searchRoomsToStay(RoomFilter filter) {
        return jdbcTemplate.query(SEARCH,
                (rs, rowNum) -> new HotelRoomInfo(
                        rs.getInt("hotel_id"),
                        rs.getString("hotel_name"),
                        rs.getInt("room_id"),
                        rs.getInt("hotel_content_id")
                ),
                filter.checkIn(),
                filter.checkOut(),
                filter.checkIn(),
                filter.checkOut(),
                filter.checkIn(),
                filter.country(),
                filter.locality(),
                filter.costFrom(),
                filter.costTo(),
                filter.adultBedCount(),
                filter.childrenBedCount());
    }

    @Override
    public List<Room> findAllByFilter(RoomFilter filter) {
        var predicate = QPredicates.builder()
                .add(filter.costFrom(), room.cost::goe)
                .add(filter.costTo(), room.cost::loe)
                .build();

        return new JPAQuery<Room>(entityManager)
                .select(room)
                .from(room)
                .where(predicate)
                .setHint(GraphSemantic.FETCH.getJakartaHintName(), entityManager.getEntityGraph("Room.roomContents"))
                .orderBy(room.cost.asc())
                .fetch();
    }
}
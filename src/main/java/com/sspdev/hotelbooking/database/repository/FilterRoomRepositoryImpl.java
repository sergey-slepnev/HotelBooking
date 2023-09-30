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

    private static final String CHECK = """
            SELECT DISTINCT h.id                                            hotel_id,
                            h.name                                          hotel_name,
                            COUNT(room.available)                           available_rooms,
                            FIRST_VALUE(hc.link) OVER (PARTITION BY h.name) photo
            FROM hotel h
                     JOIN hotel_details hd ON h.id = hd.hotel_id
                     JOIN room ON hd.hotel_id = room.hotel_id
                     LEFT JOIN hotel_content hc ON h.id = hc.hotel_id
            WHERE room.available = 'true'
              AND (hc.type = 'PHOTO' OR hc.type IS NULL)
              AND hd.country = ?
              AND hd.locality = ?
              AND hd.star = ?
              AND room.type = ?
              AND room.cost BETWEEN ? AND ?
              AND room.adult_bed_count BETWEEN ? AND ?
            GROUP BY h.name, hc.link, h.id;
            """;

    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    public List<HotelRoomInfo> check(RoomFilter filter) {
        return jdbcTemplate.query(CHECK,
                (rs, rowNum) -> new HotelRoomInfo(
                        rs.getInt("hotel_id"),
                        rs.getString("hotel_name"),
                        rs.getInt("available_rooms"),
                        rs.getString("photo")
                ), filter.country(), filter.locality());
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
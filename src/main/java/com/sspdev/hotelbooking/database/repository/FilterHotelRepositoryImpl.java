package com.sspdev.hotelbooking.database.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.sspdev.hotelbooking.database.entity.Hotel;
import com.sspdev.hotelbooking.database.entity.QHotel;
import com.sspdev.hotelbooking.dto.HotelInfo;
import com.sspdev.hotelbooking.dto.filter.HotelFilter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.graph.GraphSemantic;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.sspdev.hotelbooking.database.entity.QHotel.hotel;
import static com.sspdev.hotelrepository.database.querydsl.QPredicates.*;

@RequiredArgsConstructor
public class FilterHotelRepositoryImpl implements FilterHotelRepository {

    private static final String FIND_TOP_FIVE = """
            SELECT DISTINCT h.id,
                            h.name,
                            hd.star,
                            FIRST_VALUE(hc.link) OVER (PARTITION BY h.id) photo_link,
                            hc.type,
                            ROUND(AVG(r.rating), 1)                       avg_rating
            FROM hotel h
                     JOIN review r ON h.id = r.hotel_id
                     JOIN hotel_details hd ON h.id = hd.hotel_id
                     LEFT JOIN hotel_content hc ON h.id = hc.hotel_id
            WHERE hc.type = 'PHOTO'
               OR hc.link IS NULL
            GROUP BY h.id, h.name, hd.star, hc.link, hc.type
            ORDER BY avg_rating DESC
            LIMIT 5;
            """;

    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<HotelInfo> findTopFiveByRatingWithDetailsAndFirstPhoto() {
        return jdbcTemplate.query(FIND_TOP_FIVE,
                (rs, rowNum) -> new HotelInfo(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("star"),
                        rs.getString("photo_link"),
                        rs.getString("type"),
                        rs.getDouble("avg_rating")
                ));
    }

    @Override
    public List<Hotel> findAllByFilter(HotelFilter filter) {
        var predicate = builder()
                .add(filter.name(), hotel.name::eq)
                .add(filter.status(), hotel.status::eq)
                .add(filter.available(), hotel.available::eq)
                .add(filter.country(), hotel.hotelDetails.country::eq)
                .add(filter.locality(), hotel.hotelDetails.locality::eq)
                .add(filter.star(), hotel.hotelDetails.star::eq)
                .build();

        return new JPAQuery<Hotel>(entityManager)
                .select(hotel)
                .from(hotel)
                .where(hotel.available.eq(true))
                .where(predicate)
                .setHint(GraphSemantic.FETCH.getJakartaHintName(), entityManager.getEntityGraph("Hotel.hotelDetails"))
                .orderBy(hotel.hotelDetails.star.asc())
                .fetch();
    }
}
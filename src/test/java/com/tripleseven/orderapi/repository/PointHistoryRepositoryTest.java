package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private WrappingRepository wrappingRepository;

    private PointHistory pointHistory;
    private PointHistory useHistory;

    @BeforeEach
    void setUp() {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        wrappingRepository.save(wrapping);

        pointHistory = PointHistory.ofCreate(
                HistoryTypes.EARN,
                1000,
                "Earned points for book purchase",
                1L
        );
        useHistory = PointHistory.ofCreate(
                HistoryTypes.SPEND,
                -1000,
                "Used points for book purchase",
                2L
        );

    }

    @Test
    void testSavePointHistory() {
        // when
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        // then
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getId()).isNotNull();
        assertThat(savedHistory.getTypes()).isEqualTo(HistoryTypes.EARN);
        assertThat(savedHistory.getAmount()).isEqualTo(1000);
        assertThat(savedHistory.getComment()).isEqualTo("Earned points for book purchase");
        assertThat(savedHistory.getMemberId()).isEqualTo(1L);
        assertThat(savedHistory.getChangedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testFindPointHistoryById() {
        // given
        PointHistory savedHistory = pointHistoryRepository.save(useHistory);

        // when
        Optional<PointHistory> retrievedHistory = pointHistoryRepository.findById(savedHistory.getId());

        // then
        assertThat(retrievedHistory).isPresent();
        PointHistory foundHistory = retrievedHistory.get();
        assertThat(foundHistory.getTypes()).isEqualTo(HistoryTypes.SPEND);
        assertThat(foundHistory.getAmount()).isEqualTo(-1000);
        assertThat(foundHistory.getComment()).isEqualTo("Used points for book purchase");
        assertThat(foundHistory.getMemberId()).isEqualTo(2L);
    }

    @Test
    void testUpdatePointHistory() {
        // given

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        // when
        savedHistory.ofUpdate(HistoryTypes.SPEND, 700, "Used points for partial payment");
        PointHistory updatedHistory = pointHistoryRepository.save(savedHistory);

        // then
        assertThat(updatedHistory.getTypes()).isEqualTo(HistoryTypes.SPEND);
        assertThat(updatedHistory.getAmount()).isEqualTo(700);
        assertThat(updatedHistory.getComment()).isEqualTo("Used points for partial payment");
        assertThat(updatedHistory.getChangedAt()).isAfterOrEqualTo(savedHistory.getChangedAt());
    }

    @Test
    void testDeletePointHistory() {
        // given

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        // when
        pointHistoryRepository.deleteById(savedHistory.getId());

        // then
        Optional<PointHistory> deletedHistory = pointHistoryRepository.findById(savedHistory.getId());
        assertThat(deletedHistory).isNotPresent();
    }
}
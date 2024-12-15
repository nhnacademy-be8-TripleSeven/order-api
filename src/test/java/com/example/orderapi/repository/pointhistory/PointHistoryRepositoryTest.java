package com.example.orderapi.repository.pointhistory;

import com.example.orderapi.entity.pointhistory.PointHistory;
import com.example.orderapi.entity.pointhistory.HistoryTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("MemberId로 PointHistory 페이징 조회")
    void findAllByMemberId() {
        // given
        Long memberId = 1L;
        PointHistory history1 = new PointHistory(
                null, HistoryTypes.EARN, 100, LocalDateTime.now(), "Earned points", memberId);
        PointHistory history2 = new PointHistory(
                null, HistoryTypes.SPEND, 50, LocalDateTime.now().minusDays(1), "Spent points", memberId);
        pointHistoryRepository.saveAll(List.of(history1, history2));

        // when
        Page<PointHistory> result = pointHistoryRepository.findAllByMemberId(memberId, PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("amount").containsExactlyInAnyOrder(100, 50);
        assertThat(result.getContent()).extracting("comment").containsExactlyInAnyOrder("Earned points", "Spent points");
    }

    @Test
    @DisplayName("MemberId로 PointHistory 모두 삭제")
    void deleteAllByMemberId() {
        // given
        Long memberId = 1L;
        PointHistory history1 = new PointHistory(
                null, HistoryTypes.EARN, 100, LocalDateTime.now(), "Earned points", memberId);
        PointHistory history2 = new PointHistory(
                null, HistoryTypes.SPEND, 50, LocalDateTime.now().minusDays(1), "Spent points", memberId);
        pointHistoryRepository.saveAll(List.of(history1, history2));

        // when
        pointHistoryRepository.deleteAllByMemberId(memberId);
        List<PointHistory> remainingHistories = pointHistoryRepository.findAll();

        // then
        assertThat(remainingHistories).isEmpty();
    }

    @Test
    @DisplayName("MemberId로 PointHistory 합계 조회")
    void sumAmount() {
        // given
        Long memberId = 1L;
        PointHistory history1 = new PointHistory(
                null, HistoryTypes.EARN, 100, LocalDateTime.now(), "Earned points", memberId);
        PointHistory history2 = new PointHistory(
                null, HistoryTypes.SPEND, 200, LocalDateTime.now().minusDays(1), "Bonus points", memberId);
        pointHistoryRepository.saveAll(List.of(history1, history2));

        // when
        int sum = pointHistoryRepository.sumAmount(memberId);

        // then
        assertThat(sum).isEqualTo(300);
    }
}
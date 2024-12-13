package com.example.orderapi.repository;

import com.example.orderapi.repository.pointhistory.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void findAllByMemberId() {
        //given

    }

    @Test
    void deleteAllByMemberId() {
    }

    @Test
    void sumAmount() {
    }
}
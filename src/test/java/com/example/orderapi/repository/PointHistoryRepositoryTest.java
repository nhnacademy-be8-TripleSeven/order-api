package com.example.orderapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.Query;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void findAllByMemberId() {
        //given
        PointHiso
    }

    @Test
    void deleteAllByMemberId() {
    }

    @Test
    void sumAmount() {
    }
}
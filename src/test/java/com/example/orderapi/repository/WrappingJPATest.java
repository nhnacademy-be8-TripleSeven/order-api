package com.example.orderapi.repository;

import com.example.orderapi.entity.wrapping.Wrapping;
import com.example.orderapi.repository.wrapping.WrappingRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataJpaTest
class WrappingJPATest {

    @Autowired
    private WrappingRepository wrappingRepository;

    @Test
    @DisplayName("Wrapping 저장 및 조회")
    void saveAndFindById() {
        Wrapping wrapping = new Wrapping();
        wrapping.setName("test");

        Wrapping saved = wrappingRepository.save(wrapping);

        Optional<Wrapping> found = wrappingRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("Wrapping 수정")
    void updateWrapping() {
        Wrapping wrapping = new Wrapping();
        wrapping.setName("test1");
        Wrapping saved = wrappingRepository.save(wrapping);

        saved.setName("test2");
        Wrapping updated = wrappingRepository.save(saved);

        Optional<Wrapping> found = wrappingRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("test2");
    }

    @Test
    @DisplayName("Wrapping 삭제")
    void deleteWrapping() {
        Wrapping wrapping = new Wrapping();
        wrapping.setName("test");
        Wrapping saved = wrappingRepository.save(wrapping);

        wrappingRepository.deleteById(saved.getId());

        Optional<Wrapping> found = wrappingRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }
}

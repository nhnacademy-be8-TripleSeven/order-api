package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WrappingRepositoryTest {

    @Autowired
    private WrappingRepository wrappingRepository;

    private Wrapping wrapping;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
    }

    @Test
    void testSaveWrapping() {
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        assertNotNull(savedWrapping.getId());
        assertEquals("Test Wrapping", savedWrapping.getName());
        assertEquals(100, savedWrapping.getPrice());
    }

    @Test
    void testFindById() {
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        Optional<Wrapping> foundWrapping = wrappingRepository.findById(savedWrapping.getId());

        assertTrue(foundWrapping.isPresent());
        assertEquals("Test Wrapping", foundWrapping.get().getName());
        assertEquals(100, foundWrapping.get().getPrice());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Wrapping> foundWrapping = wrappingRepository.findById(999L);
        assertFalse(foundWrapping.isPresent());
    }

    @Test
    void testDeleteWrapping() {
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        wrappingRepository.delete(savedWrapping);

        Optional<Wrapping> foundWrapping = wrappingRepository.findById(savedWrapping.getId());
        assertFalse(foundWrapping.isPresent());
    }

    @Test
    void testUpdateWrapping() {
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        savedWrapping.ofUpdate("Updated Wrapping", 200);

        Wrapping updatedWrapping = wrappingRepository.save(savedWrapping);

        assertEquals("Updated Wrapping", updatedWrapping.getName());
        assertEquals(200, updatedWrapping.getPrice());
    }

    @Test
    void testExistsById() {
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        boolean exists = wrappingRepository.existsById(savedWrapping.getId());

        assertTrue(exists);
    }
}
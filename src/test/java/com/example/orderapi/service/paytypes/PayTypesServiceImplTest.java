package com.example.orderapi.service.paytypes;

import com.example.orderapi.dto.paytypes.PayTypesResponse;
import com.example.orderapi.entity.paytypes.PayTypes;
import com.example.orderapi.exception.notfound.impl.PayTypeNotFoundException;
import com.example.orderapi.repository.paytypes.PayTypesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayTypesServiceImplTest {

    @Mock
    private PayTypesRepository payTypesRepository;

    @InjectMocks
    private PayTypesServiceImpl payTypesService;

    private PayTypes payTypes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payTypes = new PayTypes();
        payTypes.setId(1L);
        payTypes.setName("Credit Card");
    }

    @Test
    void testGetAllPayTypes_WhenNoPayTypesFound() {
        // Arrange
        when(payTypesRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        PayTypeNotFoundException exception = assertThrows(PayTypeNotFoundException.class, () -> {
            payTypesService.getAllPayTypes();
        });
        assertEquals("No PayTypes found.", exception.getMessage());
    }

    @Test
    void testGetAllPayTypes_WhenPayTypesFound() {
        // Arrange
        PayTypes payType = new PayTypes();
        payType.setId(1L);
        payType.setName("Credit Card");
        when(payTypesRepository.findAll()).thenReturn(List.of(payType));

        // Act
        var result = payTypesService.getAllPayTypes();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Credit Card", result.get(0).getName());
    }

    @Test
    void testCreatePayType() {
        // Arrange
        PayTypes payTypeToSave = new PayTypes();
        payTypeToSave.setName("PayPal");

        when(payTypesRepository.save(payTypeToSave)).thenReturn(payTypes);

        // Act
        PayTypesResponse result = payTypesService.createPayType(payTypeToSave);

        // Assert
        assertNotNull(result);
        assertEquals("Credit Card", result.getName());
    }

    @Test
    void testGetPayTypeById_WhenPayTypeNotFound() {
        // Arrange
        Long payTypeId = 1L;
        when(payTypesRepository.findById(payTypeId)).thenReturn(Optional.empty());

        // Act & Assert
        PayTypeNotFoundException exception = assertThrows(PayTypeNotFoundException.class, () -> {
            payTypesService.getPayTypeById(payTypeId);
        });
        assertEquals("PayType with id 1 not found.", exception.getMessage());
    }

    @Test
    void testGetPayTypeById_WhenPayTypeFound() {
        // Arrange
        Long payTypeId = 1L;
        when(payTypesRepository.findById(payTypeId)).thenReturn(Optional.of(payTypes));

        // Act
        PayTypesResponse result = payTypesService.getPayTypeById(payTypeId);

        // Assert
        assertNotNull(result);
        assertEquals("Credit Card", result.getName());
    }

    @Test
    void testRemovePayType_WhenPayTypeNotFound() {
        // Arrange
        Long payTypeId = 1L;
        when(payTypesRepository.existsById(payTypeId)).thenReturn(false);

        // Act & Assert
        PayTypeNotFoundException exception = assertThrows(PayTypeNotFoundException.class, () -> {
            payTypesService.removePayType(payTypeId);
        });
        assertEquals("PayType with id 1 not found.", exception.getMessage());
    }

    @Test
    void testRemovePayType_WhenPayTypeFound() {
        // Arrange
        Long payTypeId = 1L;
        when(payTypesRepository.existsById(payTypeId)).thenReturn(true);

        // Act
        payTypesService.removePayType(payTypeId);

        // Assert
        verify(payTypesRepository, times(1)).deleteById(payTypeId);
    }

    @Test
    void testUpdatePayType_WhenPayTypeNotFound() {
        // Arrange
        Long payTypeId = 1L;
        payTypes.setName("Updated PayType");
        when(payTypesRepository.findById(payTypeId)).thenReturn(Optional.empty());

        // Act & Assert
        PayTypeNotFoundException exception = assertThrows(PayTypeNotFoundException.class, () -> {
            payTypesService.updatePayType(payTypes);
        });
        assertEquals("PayType with id 1 not found.", exception.getMessage());
    }

    @Test
    void testUpdatePayType_WhenPayTypeFound() {
        // Arrange
        Long payTypeId = 1L;
        payTypes.setName("Updated PayType");
        when(payTypesRepository.findById(payTypeId)).thenReturn(Optional.of(payTypes));
        when(payTypesRepository.save(payTypes)).thenReturn(payTypes);

        // Act
        PayTypesResponse result = payTypesService.updatePayType(payTypes);

        // Assert
        assertNotNull(result);
        assertEquals("Updated PayType", result.getName());
    }

    @Test
    void testCreatePayType_WithInvalidData() {
        // Arrange
        PayTypes invalidPayType = new PayTypes();
        invalidPayType.setName("");  // Invalid name

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            payTypesService.createPayType(invalidPayType);
        });
        assertEquals("PayType name cannot be null or empty.", exception.getMessage());
    }
}
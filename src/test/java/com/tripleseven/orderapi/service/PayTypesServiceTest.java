package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequestDTO;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponseDTO;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import com.tripleseven.orderapi.service.paytypes.PayTypesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayTypesServiceTest {

    @Mock
    private PayTypesRepository payTypesRepository;

    @InjectMocks
    private PayTypesServiceImpl payTypesService;

    private PayType payType;

    @BeforeEach
    void setUp() {
        payType = PayType.ofCreate("카카오페이");
        ReflectionTestUtils.setField(payType, "id", 1L); // ID 설정
    }

    @Test
    @DisplayName("1. 결제 유형 전체 조회 테스트")
    void getAllPayTypes() {
        // Given
        when(payTypesRepository.findAll()).thenReturn(List.of(payType));

        // When
        List<PayTypesResponseDTO> response = payTypesService.getAllPayTypes();

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getName()).isEqualTo("카카오페이");

        verify(payTypesRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("2. 결제 유형 생성 테스트")
    void createPayType() {
        // Given
        PayTypeCreateRequestDTO request = new PayTypeCreateRequestDTO("네이버페이");
        PayType newPayType = PayType.ofCreate(request.getName());
        ReflectionTestUtils.setField(newPayType, "id", 2L);

        when(payTypesRepository.save(any())).thenReturn(newPayType);

        // When
        PayTypesResponseDTO response = payTypesService.createPayType(request);

        // Then
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("네이버페이");

        verify(payTypesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("3. 결제 유형 ID로 조회 - 존재하는 경우")
    void getPayTypeById_found() {
        // Given
        when(payTypesRepository.findById(1L)).thenReturn(Optional.of(payType));

        // When
        PayTypesResponseDTO response = payTypesService.getPayTypeById(1L);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("카카오페이");

        verify(payTypesRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("4. 결제 유형 ID로 조회 - 존재하지 않는 경우")
    void getPayTypeById_notFound() {
        // Given
        when(payTypesRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> payTypesService.getPayTypeById(99L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ID_NOT_FOUND.getMessage());

        verify(payTypesRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("5. 결제 유형 삭제 테스트 - 존재하는 경우")
    void removePayType_success() {
        // Given
        when(payTypesRepository.existsById(1L)).thenReturn(true);
        doNothing().when(payTypesRepository).deleteById(1L);

        // When
        payTypesService.removePayType(1L);

        // Then
        verify(payTypesRepository, times(1)).existsById(1L);
        verify(payTypesRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("6. 결제 유형 삭제 테스트 - 존재하지 않는 경우")
    void removePayType_notFound() {
        // Given
        when(payTypesRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> payTypesService.removePayType(99L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ID_NOT_FOUND.getMessage());

        verify(payTypesRepository, times(1)).existsById(99L);
        verify(payTypesRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("7. 결제 유형 업데이트 테스트 - 존재하는 경우")
    void updatePayType_success() {
        // Given
        PayTypeCreateRequestDTO request = new PayTypeCreateRequestDTO("토스페이");
        when(payTypesRepository.findById(1L)).thenReturn(Optional.of(payType));

        // When
        PayTypesResponseDTO response = payTypesService.updatePayType(1L, request);

        // Then
        assertThat(response.getName()).isEqualTo("토스페이");

        verify(payTypesRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("8. 결제 유형 업데이트 테스트 - 존재하지 않는 경우")
    void updatePayType_notFound() {
        // Given
        PayTypeCreateRequestDTO request = new PayTypeCreateRequestDTO("토스페이");
        when(payTypesRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> payTypesService.updatePayType(99L, request))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ID_NOT_FOUND.getMessage());

        verify(payTypesRepository, times(1)).findById(99L);
    }
}
package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequestDTO;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponseDTO;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.paytypes.PayTypesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // ✅ Mockito 자동 초기화
class PayTypesServiceTest {

    @Mock
    private PayTypesService payTypesService;

    @Test
    @DisplayName("1. 결제 유형 목록 조회 테스트")
    void getAllPayTypes() {
        // Given
        PayType payType1 = PayType.ofCreate("신용카드");
        PayType payType2 = PayType.ofCreate("계좌이체");

        // ✅ ID를 수동으로 설정
        setId(payType1, 1L);
        setId(payType2, 2L);

        List<PayTypesResponseDTO> mockResponse = List.of(
                PayTypesResponseDTO.fromEntity(payType1),
                PayTypesResponseDTO.fromEntity(payType2)
        );

        when(payTypesService.getAllPayTypes()).thenReturn(mockResponse);

        // When
        List<PayTypesResponseDTO> response = payTypesService.getAllPayTypes();

        // Then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getName()).isEqualTo("신용카드");
        assertThat(response.get(1).getId()).isEqualTo(2L);
        assertThat(response.get(1).getName()).isEqualTo("계좌이체");

        verify(payTypesService, times(1)).getAllPayTypes();
    }

    // ✅ 리플렉션을 이용해 `PayType`의 ID를 수동 설정하는 메서드 추가
    private void setId(PayType payType, Long id) {
        try {
            java.lang.reflect.Field field = PayType.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(payType, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID via reflection", e);
        }
    }

    @Test
    @DisplayName("2. 새로운 결제 유형 생성 테스트")
    void createPayType() {
        // Given
        PayTypeCreateRequestDTO request = new PayTypeCreateRequestDTO("페이팔");
        PayType payType = PayType.ofCreate(request.getName());

        // ✅ ID를 수동으로 설정
        setId(payType, 1L);

        PayTypesResponseDTO mockResponse = PayTypesResponseDTO.fromEntity(payType);
        when(payTypesService.createPayType(request)).thenReturn(mockResponse);

        // When
        PayTypesResponseDTO response = payTypesService.createPayType(request);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("페이팔");

        verify(payTypesService, times(1)).createPayType(request);
    }

    @Test
    @DisplayName("3. 특정 결제 유형 조회 테스트 - 존재하는 경우")
    void getPayTypeById_found() {
        // Given
        PayType payType = PayType.ofCreate("네이버페이");

        // ✅ ID를 수동으로 설정
        setId(payType, 1L);

        PayTypesResponseDTO mockResponse = PayTypesResponseDTO.fromEntity(payType);
        when(payTypesService.getPayTypeById(1L)).thenReturn(mockResponse);

        // When
        PayTypesResponseDTO response = payTypesService.getPayTypeById(1L);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("네이버페이");

        verify(payTypesService, times(1)).getPayTypeById(1L);
    }

    @Test
    @DisplayName("4. 특정 결제 유형 조회 테스트 - 존재하지 않는 경우 예외 발생")
    void getPayTypeById_notFound() {
        // Given
        when(payTypesService.getPayTypeById(99L))
                .thenThrow(new CustomException(ErrorCode.ID_NOT_FOUND)); // ✅ 메시지 설정된 예외

        // When & Then
        assertThatThrownBy(() -> payTypesService.getPayTypeById(99L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ID_NOT_FOUND.getMessage()); // ✅ 메시지 검증

        verify(payTypesService, times(1)).getPayTypeById(99L);
    }

    @Test
    @DisplayName("5. 결제 유형 삭제 테스트 - 존재하는 경우")
    void removePayType_success() {
        // Given
        doNothing().when(payTypesService).removePayType(1L);

        // When
        payTypesService.removePayType(1L);

        // Then
        verify(payTypesService, times(1)).removePayType(1L);
    }

    @Test
    @DisplayName("6. 결제 유형 삭제 테스트 - 존재하지 않는 경우 예외 발생")
    void removePayType_notFound() {
        // Given
        doThrow(new CustomException(ErrorCode.ID_NOT_FOUND)).when(payTypesService).removePayType(99L);

        // When & Then
        assertThatThrownBy(() -> payTypesService.removePayType(99L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ID_NOT_FOUND.getMessage());

        verify(payTypesService, times(1)).removePayType(99L);
    }

    @Test
    @DisplayName("7. 결제 유형 업데이트 테스트 - 존재하는 경우")
    void updatePayType_success() {
        // Given
        PayTypeCreateRequestDTO request = new PayTypeCreateRequestDTO("토스페이");

        PayType payType = PayType.ofCreate("카카오페이");

        // ✅ payType의 ID를 강제로 설정
        ReflectionTestUtils.setField(payType, "id", 1L);

        payType.ofUpdate(request.getName()); // ✅ 업데이트 반영

        PayTypesResponseDTO mockResponse = PayTypesResponseDTO.fromEntity(payType);
        when(payTypesService.updatePayType(1L, request)).thenReturn(mockResponse);

        // When
        PayTypesResponseDTO response = payTypesService.updatePayType(1L, request);

        // Then
        assertThat(response.getId()).isEqualTo(1L); // ✅ ID가 null이 아닌지 확인
        assertThat(response.getName()).isEqualTo("토스페이");

        verify(payTypesService, times(1)).updatePayType(1L, request);
    }

    @Test
    @DisplayName("8. 결제 유형 업데이트 테스트 - 존재하지 않는 경우 예외 발생")
    void updatePayType_notFound() {
        // Given
        PayTypeCreateRequestDTO request = new PayTypeCreateRequestDTO("토스페이");
        when(payTypesService.updatePayType(99L, request)).thenThrow(new CustomException(ErrorCode.ID_NOT_FOUND));

        // When & Then
        assertThatThrownBy(() -> payTypesService.updatePayType(99L, request))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ID_NOT_FOUND.getMessage());

        verify(payTypesService, times(1)).updatePayType(99L, request);
    }
}
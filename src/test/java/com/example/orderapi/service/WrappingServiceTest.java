package com.example.orderapi.service;

import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.dto.wrapping.WrappingUpdateRequest;
import com.example.orderapi.entity.wrapping.Wrapping;
import com.example.orderapi.repository.wrapping.WrappingRepository;
import com.example.orderapi.service.wrapping.WrappingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WrappingServiceTest {

    @Mock
    private WrappingRepository wrappingRepository;

    @InjectMocks
    private WrappingServiceImpl wrappingService;

    @Test
    @DisplayName("Wrapping Id로 조회 - 성공")
    void getById_Success() {
        Wrapping wrapping = new Wrapping();
        wrapping.setId(1L);
        wrapping.setName("test");
        wrapping.setPrice(1000);
        when(wrappingRepository.findById(1L)).thenReturn(Optional.of(wrapping));

        WrappingResponse response = wrappingService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("test");
        verify(wrappingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Wrapping Id로 조회 - 실패")
    void getById_Fail() {
        when(wrappingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wrappingService.getById(1L))
                .isInstanceOf(RuntimeException.class);
        verify(wrappingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Wrapping 목록 조회 - 성공")
    void getAllToList_Success() {
        // Given
        Wrapping wrapping1 = new Wrapping();
        wrapping1.setId(1L);
        wrapping1.setName("test1");
        wrapping1.setPrice(1000);

        Wrapping wrapping2 = new Wrapping();
        wrapping2.setId(2L);
        wrapping2.setName("test2");
        wrapping2.setPrice(2000);

        when(wrappingRepository.findAll()).thenReturn(List.of(wrapping1, wrapping2));

        List<WrappingResponse> responses = wrappingService.getAllToList();

        assertThat(responses).hasSize(2);
        assertThat(responses.getFirst().getName()).isEqualTo("test1");
        verify(wrappingRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Wrapping 생성")
    void create() {
        WrappingCreateRequest request = new WrappingCreateRequest("test", 1500);

        Wrapping wrapping = new Wrapping();
        wrapping.setId(1L);
        wrapping.setName(request.getName());
        wrapping.setPrice(request.getPrice());

        when(wrappingRepository.save(any(Wrapping.class))).thenReturn(wrapping);

        WrappingResponse response = wrappingService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("test");
        verify(wrappingRepository, times(1)).save(any(Wrapping.class));
    }

    @Test
    @DisplayName("Wrapping 업데이트")
    void update() {
        Wrapping wrapping = new Wrapping();
        wrapping.setId(1L);
        wrapping.setName("test1");
        wrapping.setPrice(1000);

        WrappingUpdateRequest request = new WrappingUpdateRequest("test2", 2000);

        when(wrappingRepository.findById(1L)).thenReturn(Optional.of(wrapping));
        when(wrappingRepository.save(any(Wrapping.class))).thenAnswer(i -> i.getArgument(0));

        WrappingResponse response = wrappingService.update(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("test2");
        verify(wrappingRepository, times(1)).findById(1L);
        verify(wrappingRepository, times(1)).save(any(Wrapping.class));
    }

    @Test
    @DisplayName("Wrapping 삭제")
    void delete() {
        doNothing().when(wrappingRepository).deleteById(1L);

        wrappingService.delete(1L);

        verify(wrappingRepository, times(1)).deleteById(1L);
    }
}
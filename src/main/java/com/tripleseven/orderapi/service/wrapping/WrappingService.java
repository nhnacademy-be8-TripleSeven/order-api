package com.tripleseven.orderapi.service.wrapping;

import com.tripleseven.orderapi.dto.wrapping.WrappingCreateRequestDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingUpdateRequestDTO;

import java.util.List;

public interface WrappingService {
    WrappingResponseDTO getWrappingById(Long id);

    List<WrappingResponseDTO> getWrappingsToList();

    WrappingResponseDTO createWrapping(WrappingCreateRequestDTO wrappingCreateRequestDTO);

    WrappingResponseDTO updateWrapping(Long id, WrappingUpdateRequestDTO wrappingUpdateRequestDTO);

    void deleteWrapping(Long id);
}

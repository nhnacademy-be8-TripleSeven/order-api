package com.example.orderapi.service.wrapping;

import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.dto.wrapping.WrappingUpdateRequest;

import java.util.List;

public interface WrappingService {
    WrappingResponse getWrappingById(Long id);

    List<WrappingResponse> getWrappingsToList();

    WrappingResponse createWrapping(WrappingCreateRequest wrappingCreateRequest);

    WrappingResponse updateWrapping(Long id, WrappingUpdateRequest wrappingUpdateRequest);

    void deleteWrapping(Long id);
}

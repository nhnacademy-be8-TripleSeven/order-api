package com.example.orderapi.service.wrapping;

import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.dto.wrapping.WrappingUpdateRequest;

import java.util.List;

public interface WrappingService {
    WrappingResponse getById(Long id);

    List<WrappingResponse> getAllToList();

    WrappingResponse create(WrappingCreateRequest wrappingCreateRequest);

    WrappingResponse update(Long id, WrappingUpdateRequest wrappingUpdateRequest);

    void delete(Long id);
}

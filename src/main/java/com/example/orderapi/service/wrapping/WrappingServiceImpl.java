package com.example.orderapi.service.wrapping;

import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.dto.wrapping.WrappingUpdateRequest;
import com.example.orderapi.entity.Wrapping;
import com.example.orderapi.repository.wrapping.WrappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WrappingServiceImpl implements WrappingService {

    private final WrappingRepository wrappingRepository;

    @Override
    public WrappingResponse getById(Long id) {
        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(id);

        if (optionalWrapping.isEmpty()) {
            throw new RuntimeException();
        }

        return WrappingResponse.fromEntity(optionalWrapping.get());
    }

    @Override
    public List<WrappingResponse> getAllToList() {
        List<Wrapping> list = wrappingRepository.findAll();

        if (list.isEmpty()) {
            throw new RuntimeException();
        }

        return list.stream().map(WrappingResponse::fromEntity).toList();
    }

    @Override
    public WrappingResponse create(WrappingCreateRequest wrappingCreateRequest) {
        Wrapping wrapping = new Wrapping();
        wrapping.setName(wrappingCreateRequest.getName());
        wrapping.setPrice(wrappingCreateRequest.getPrice());
        Wrapping createWrapping = wrappingRepository.save(wrapping);

        return WrappingResponse.fromEntity(createWrapping);
    }

    @Override
    public WrappingResponse update(Long id, WrappingUpdateRequest wrappingUpdateRequest) {
        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(id);
        if (optionalWrapping.isEmpty()) {
            throw new RuntimeException();
        }
        Wrapping wrapping = optionalWrapping.get();
        wrapping.setName(wrappingUpdateRequest.getName());
        wrapping.setPrice(wrappingUpdateRequest.getPrice());
        Wrapping updateWrapping = wrappingRepository.save(wrapping);

        return WrappingResponse.fromEntity(updateWrapping);
    }

    @Override
    public void delete(Long id) {
        wrappingRepository.deleteById(id);
    }
}

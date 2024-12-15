package com.example.orderapi.service.wrapping;

import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.dto.wrapping.WrappingUpdateRequest;
import com.example.orderapi.entity.wrapping.Wrapping;
import com.example.orderapi.repository.wrapping.WrappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class WrappingServiceImpl implements WrappingService {

    private final WrappingRepository wrappingRepository;

    @Override
    public WrappingResponse getWrappingById(Long id) {
        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(id);

        if (optionalWrapping.isEmpty()) {
            throw new RuntimeException();
        }

        return WrappingResponse.fromEntity(optionalWrapping.get());
    }

    @Override
    public List<WrappingResponse> getWrappingsToList() {
        List<Wrapping> list = wrappingRepository.findAll();

        if (list.isEmpty()) {
            throw new RuntimeException();
        }

        return list.stream().map(WrappingResponse::fromEntity).toList();
    }

    @Override
    public WrappingResponse createWrapping(WrappingCreateRequest wrappingCreateRequest) {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate(wrappingCreateRequest.getName(), wrappingCreateRequest.getPrice());
        Wrapping createWrapping = wrappingRepository.save(wrapping);

        return WrappingResponse.fromEntity(createWrapping);
    }

    @Override
    public WrappingResponse updateWrapping(Long id, WrappingUpdateRequest wrappingUpdateRequest) {
        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(id);
        if (optionalWrapping.isEmpty()) {
            throw new RuntimeException();
        }
        Wrapping wrapping = optionalWrapping.get();
        wrapping.ofUpdate(wrappingUpdateRequest.getName(), wrappingUpdateRequest.getPrice());

        return WrappingResponse.fromEntity(wrapping);
    }

    @Override
    public void deleteWrapping(Long id) {
        if (!wrappingRepository.existsById(id)) {
            throw new RuntimeException();
        }
        wrappingRepository.deleteById(id);
    }
}

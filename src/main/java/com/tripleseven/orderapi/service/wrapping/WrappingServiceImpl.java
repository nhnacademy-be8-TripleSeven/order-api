package com.tripleseven.orderapi.service.wrapping;

import com.tripleseven.orderapi.dto.wrapping.WrappingCreateRequestDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingUpdateRequestDTO;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.notfound.WrappingNotFoundException;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WrappingServiceImpl implements WrappingService {

    private final WrappingRepository wrappingRepository;

    @Override
    @Transactional(readOnly = true)
    public WrappingResponseDTO getWrappingById(Long id) {
        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(id);

        if (optionalWrapping.isEmpty()) {
            throw new WrappingNotFoundException(id);
        }

        return WrappingResponseDTO.fromEntity(optionalWrapping.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WrappingResponseDTO> getWrappingsToList() {
        List<Wrapping> list = wrappingRepository.findAll();

        if (list.isEmpty()) {
            return List.of();
        }

        return list.stream().map(WrappingResponseDTO::fromEntity).toList();
    }

    @Override
    @Transactional
    public WrappingResponseDTO createWrapping(WrappingCreateRequestDTO wrappingCreateRequestDTO) {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate(wrappingCreateRequestDTO.getName(), wrappingCreateRequestDTO.getPrice());
        Wrapping createWrapping = wrappingRepository.save(wrapping);

        return WrappingResponseDTO.fromEntity(createWrapping);
    }

    @Override
    @Transactional
    public WrappingResponseDTO updateWrapping(Long id, WrappingUpdateRequestDTO wrappingUpdateRequestDTO) {
        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(id);
        if (optionalWrapping.isEmpty()) {
            throw new WrappingNotFoundException(id);
        }
        Wrapping wrapping = optionalWrapping.get();
        wrapping.ofUpdate(wrappingUpdateRequestDTO.getName(), wrappingUpdateRequestDTO.getPrice());

        return WrappingResponseDTO.fromEntity(wrapping);
    }

    @Override
    @Transactional
    public void deleteWrapping(Long id) {
        if (!wrappingRepository.existsById(id)) {
            throw new WrappingNotFoundException(id);
        }
        wrappingRepository.deleteById(id);
    }
}

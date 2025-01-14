package com.tripleseven.orderapi.service.wrapping;

import com.tripleseven.orderapi.dto.wrapping.WrappingCreateRequestDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingUpdateRequestDTO;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WrappingServiceImpl implements WrappingService {

    private final WrappingRepository wrappingRepository;

    @Override
    @Transactional(readOnly = true)
    public WrappingResponseDTO getWrappingById(Long id) {
        Wrapping wrapping = wrappingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));


        return WrappingResponseDTO.fromEntity(wrapping);
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
        Wrapping wrapping = wrappingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        wrapping.ofUpdate(wrappingUpdateRequestDTO.getName(), wrappingUpdateRequestDTO.getPrice());

        return WrappingResponseDTO.fromEntity(wrapping);
    }

    @Override
    @Transactional
    public void deleteWrapping(Long id) {
        if (!wrappingRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
        wrappingRepository.deleteById(id);
    }
}

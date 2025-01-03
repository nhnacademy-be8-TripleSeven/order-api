package com.tripleseven.orderapi.service.paytypes;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequestDTO;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponseDTO;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.exception.notfound.PayTypeNotFoundException;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class PayTypesServiceImpl implements PayTypesService {

    private final PayTypesRepository payTypesRepository;

    @Override
    public List<PayTypesResponseDTO> getAllPayTypes() {
        List<PayType> payTypeList = payTypesRepository.findAll();
        if (payTypeList.isEmpty()) {
            throw new PayTypeNotFoundException("No PayTypes found.");
        }
        return payTypeList.stream()
                .map(PayTypesResponseDTO::fromEntity)
                .toList();
    }

    @Override
    public PayTypesResponseDTO createPayType(PayTypeCreateRequestDTO request) {
        PayType newPayType = PayType.ofCreate(request.getName());
        PayType savedPayType = payTypesRepository.save(newPayType);
        return PayTypesResponseDTO.fromEntity(savedPayType);
    }

    @Override
    public PayTypesResponseDTO getPayTypeById(Long id) {
        Optional<PayType> payType = payTypesRepository.findById(id);
        if (payType.isEmpty()) {
            throw new PayTypeNotFoundException("PayType with id " + id + " not found.");
        }
        return PayTypesResponseDTO.fromEntity(payType.get());
    }

    @Override
    public void removePayType(Long id) {
        if (!payTypesRepository.existsById(id)) {
            throw new PayTypeNotFoundException("PayType with id " + id + " not found.");
        }
        payTypesRepository.deleteById(id);
    }

    @Override
    public PayTypesResponseDTO updatePayType(Long id, PayTypeCreateRequestDTO request) {
        Optional<PayType> existingPayType = payTypesRepository.findById(id);
        if (existingPayType.isEmpty()) {
            throw new PayTypeNotFoundException("PayType with id " + id + " not found.");
        }

        PayType updatedPayType = existingPayType.get().ofUpdate(request.getName());
        PayType savedPayType = payTypesRepository.save(updatedPayType);
        return PayTypesResponseDTO.fromEntity(savedPayType);
    }
}
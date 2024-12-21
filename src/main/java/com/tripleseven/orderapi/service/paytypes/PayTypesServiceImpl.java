package com.tripleseven.orderapi.service.paytypes;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequest;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponse;
import com.tripleseven.orderapi.entity.paytypes.PayTypes;
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
    public List<PayTypesResponse> getAllPayTypes() {
        List<PayTypes> payTypesList = payTypesRepository.findAll();
        if (payTypesList.isEmpty()) {
            throw new PayTypeNotFoundException("No PayTypes found.");
        }
        return payTypesList.stream()
                .map(PayTypesResponse::fromEntity)
                .toList();
    }

    @Override
    public PayTypesResponse createPayType(PayTypeCreateRequest request) {
        PayTypes newPayType = PayTypes.ofCreate(request.getName());
        PayTypes savedPayType = payTypesRepository.save(newPayType);
        return PayTypesResponse.fromEntity(savedPayType);
    }

    @Override
    public PayTypesResponse getPayTypeById(Long id) {
        Optional<PayTypes> payType = payTypesRepository.findById(id);
        if (payType.isEmpty()) {
            throw new PayTypeNotFoundException("PayType with id " + id + " not found.");
        }
        return PayTypesResponse.fromEntity(payType.get());
    }

    @Override
    public void removePayType(Long id) {
        if (!payTypesRepository.existsById(id)) {
            throw new PayTypeNotFoundException("PayType with id " + id + " not found.");
        }
        payTypesRepository.deleteById(id);
    }

    @Override
    public PayTypesResponse updatePayType(Long id, PayTypeCreateRequest request) {
        Optional<PayTypes> existingPayType = payTypesRepository.findById(id);
        if (existingPayType.isEmpty()) {
            throw new PayTypeNotFoundException("PayType with id " + id + " not found.");
        }

        PayTypes updatedPayType = existingPayType.get().ofUpdate(request.getName());
        PayTypes savedPayType = payTypesRepository.save(updatedPayType);
        return PayTypesResponse.fromEntity(savedPayType);
    }
}
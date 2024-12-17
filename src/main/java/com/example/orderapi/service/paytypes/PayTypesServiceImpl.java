package com.example.orderapi.service.paytypes;

import com.example.orderapi.dto.paytypes.PayTypesResponse;
import com.example.orderapi.entity.paytypes.PayTypes;
import com.example.orderapi.exception.notfound.PayTypeNotFoundException;
import com.example.orderapi.repository.paytypes.PayTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public PayTypesResponse createPayType(PayTypes payTypes) {
        if (payTypes == null || payTypes.getName() == null || payTypes.getName().isEmpty()) {
            throw new IllegalArgumentException("PayType name cannot be null or empty.");
        }
        PayTypes savedPayType = payTypesRepository.save(payTypes);
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
    public PayTypesResponse updatePayType(PayTypes payTypes) {
        if (payTypes == null || payTypes.getId() == null) {
            throw new IllegalArgumentException("PayType ID must be provided for update.");
        }

        Optional<PayTypes> existingPayType = payTypesRepository.findById(payTypes.getId());
        if (existingPayType.isEmpty()) {
            throw new PayTypeNotFoundException("PayType with id " + payTypes.getId() + " not found.");
        }

        PayTypes updatedPayType = existingPayType.get();
        updatedPayType.setName(payTypes.getName());

        PayTypes savedPayType = payTypesRepository.save(updatedPayType);
        return PayTypesResponse.fromEntity(savedPayType);
    }
}
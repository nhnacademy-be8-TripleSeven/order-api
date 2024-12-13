package com.example.orderapi.service.paytypes;

import com.example.orderapi.entity.PayTypes.PayTypes;
import com.example.orderapi.repository.paytypes.PayTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PayTypesServiceImpl implements PayTypesService {

    private final PayTypesRepository payTypesRepository;
    @Override
    public List<PayTypes> findAll() {
        return payTypesRepository.findAll();
    }

    @Override
    public PayTypes save(PayTypes payTypes) {
        return payTypesRepository.save(payTypes);
    }

    @Override
    public PayTypes findById(Long id) {
        return payTypesRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        payTypesRepository.deleteById(id);
    }

    @Override
    public void update(PayTypes payTypes) {
        payTypesRepository.save(payTypes);
    }
}

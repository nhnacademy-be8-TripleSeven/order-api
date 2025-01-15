package com.tripleseven.orderapi.service.paytypes;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequestDTO;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponseDTO;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class PayTypesServiceImpl implements PayTypesService {

    private final PayTypesRepository payTypesRepository;

    @Override
    public List<PayTypesResponseDTO> getAllPayTypes() {
        List<PayType> payTypeList = payTypesRepository.findAll();

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
        PayType payType = payTypesRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        return PayTypesResponseDTO.fromEntity(payType);
    }

    @Override
    public void removePayType(Long id) {
        if (!payTypesRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
        payTypesRepository.deleteById(id);
    }

    @Override
    public PayTypesResponseDTO updatePayType(Long id, PayTypeCreateRequestDTO request) {
        PayType payType = payTypesRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));
        payType.ofUpdate(request.getName());

        return PayTypesResponseDTO.fromEntity(payType);
    }
}
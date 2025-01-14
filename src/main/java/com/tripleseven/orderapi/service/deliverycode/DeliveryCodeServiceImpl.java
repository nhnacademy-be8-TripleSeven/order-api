package com.tripleseven.orderapi.service.deliverycode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeDTO;
import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeResponseDTO;
import com.tripleseven.orderapi.entity.deliverycode.DeliveryCode;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.deliverycode.DeliveryCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCodeServiceImpl implements DeliveryCodeService {

    private final DeliveryCodeRepository deliveryCodeRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public void saveDeliveryCode(String url) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.add("accept", "application/json;charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                DeliveryCodeResponseDTO deliveryCodeResponse = objectMapper.readValue(
                        response.getBody(),
                        DeliveryCodeResponseDTO.class
                );

                List<DeliveryCodeDTO> deliveryCodeDTOs = deliveryCodeResponse.getCompanies();

                for (DeliveryCodeDTO deliveryCodeDTO : deliveryCodeDTOs) {
                    DeliveryCode dataEntity = new DeliveryCode();
                    dataEntity.ofCreate(deliveryCodeDTO.getCode(), deliveryCodeDTO.isInternational(), deliveryCodeDTO.getName());
                    deliveryCodeRepository.save(dataEntity);
                }
            } else {
                log.error("Failed to fetch data: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_BAD_REQUEST);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getDeliveryCodeToName(String name) {
        DeliveryCode deliveryCode = deliveryCodeRepository.findDeliveryCodeByName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        return deliveryCode.getId();
    }
}

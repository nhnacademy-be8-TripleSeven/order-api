package com.tripleseven.orderapi.service.deliverycode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeDTO;
import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeResponseDTO;
import com.tripleseven.orderapi.entity.deliverycode.DeliveryCode;
import com.tripleseven.orderapi.repository.deliverycode.DeliveryCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryCodeServiceImpl implements DeliveryCodeService {

    private final DeliveryCodeRepository deliveryCodeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveDeliveryCode(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();

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
                System.err.println("Failed to fetch data: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDeliveryCodeToName(String name) {
        Optional<DeliveryCode> deliveryCode = deliveryCodeRepository.findDeliveryCodeByName(name);
        if (deliveryCode.isEmpty()){
            throw new RuntimeException();
        }

        return deliveryCode.get().getId();
    }
}

package com.tripleseven.orderapi.repository.deliverycode;

import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeResponseDTO;
import com.tripleseven.orderapi.entity.deliverycode.DeliveryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryCodeRepository extends JpaRepository<DeliveryCode, Long> {
    Optional<DeliveryCode> findDeliveryCodeByName(@NotNull @NotBlank String name);
}

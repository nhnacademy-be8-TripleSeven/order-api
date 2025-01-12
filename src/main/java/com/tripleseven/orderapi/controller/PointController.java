package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @PostMapping("/points/default-policy/register")
    public ResponseEntity<PointHistoryResponseDTO> createRegisterPointHistory(
            @RequestHeader("X-USER") Long userId
    ) {
        PointHistoryResponseDTO dto = pointService.createRegisterPointHistory(userId);
        return ResponseEntity.ok(dto);
    }
}

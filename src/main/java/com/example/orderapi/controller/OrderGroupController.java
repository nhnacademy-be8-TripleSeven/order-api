package com.example.orderapi.controller;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateRequest;
import com.example.orderapi.service.OrderGroupService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderGroupController {
    private final OrderGroupService orderGroupService;

    @GetMapping
    public OrderGroupResponse getOrderGroup(@PathVariable Long id){
        return orderGroupService.getById(id);
    }

    @PostMapping
    public ResponseEntity<OrderGroupResponse> createOrderGroup(@Valid @RequestBody OrderGroupCreateRequest orderGroupCreateRequest){
        OrderGroupResponse orderGroupResponse = orderGroupService.save(orderGroupCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderGroupResponse);
    }

    @PutMapping
    public ResponseEntity<OrderGroupResponse> updateOrderGroup(@PathVariable Long id,
                                                               @Valid @RequestBody OrderGroupUpdateRequest orderGroupUpdateRequest){
        OrderGroupResponse orderGroupResponse = orderGroupService.update(id, orderGroupUpdateRequest);

        return ResponseEntity.ok(orderGroupResponse);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteOrderGroup(@PathVariable Long id) {
        orderGroupService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("OrderGroup Deleted");
    }
}

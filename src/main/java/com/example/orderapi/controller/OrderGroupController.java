package com.example.orderapi.controller;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateRequest;
import com.example.orderapi.service.ordergroup.OrderGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/order-groups")
@RequiredArgsConstructor
public class OrderGroupController {
    private final OrderGroupService orderGroupService;

    @GetMapping("/{userId}")
    public Page<OrderGroupResponse> getOrderGroups(@PathVariable Long userId, Pageable pageable){
        return orderGroupService.getOrderGroupsByUserId(userId, pageable);
    }

    @PostMapping
    public ResponseEntity<OrderGroupResponse> createOrderGroup(@Valid @RequestBody OrderGroupCreateRequest orderGroupCreateRequest){
        OrderGroupResponse orderGroupResponse = orderGroupService.create(orderGroupCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderGroupResponse);
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<OrderGroupResponse> updateOrderGroup(@PathVariable Long id,
                                                               @Valid @RequestBody OrderGroupUpdateRequest orderGroupUpdateRequest){
        OrderGroupResponse orderGroupResponse = orderGroupService.update(id, orderGroupUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(orderGroupResponse);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteOrderGroup(@PathVariable Long id) {
        orderGroupService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("OrderGroup Deleted");
    }
}

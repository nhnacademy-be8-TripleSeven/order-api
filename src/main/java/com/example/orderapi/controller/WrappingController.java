package com.example.orderapi.controller;

import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.service.wrapping.WrappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Wrapping-Controller", description = "포장지 컨트롤러")
@RestController
@RequiredArgsConstructor
public class WrappingController {

    private final WrappingService wrappingService;
    @Operation(summary = "포장지 전부 조회", description = "포장지를 전부 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공",
            content = @Content(schema = @Schema(implementation = WrappingResponse.class))),
            @ApiResponse(responseCode = "404",description = "해당하는 포장지 없음")
    })
    public ResponseEntity<List<WrappingResponse>> getAllWrappings() {
        List<WrappingResponse> responses = wrappingService.getWrappingsToList();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "포장지 생성", description = "포장지를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = WrappingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<WrappingResponse> createWrapping(@RequestBody WrappingCreateRequest request){
        WrappingResponse response = wrappingService.createWrapping(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "포장지 삭제", description = "포장지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 포장지 없음")
    })
    public ResponseEntity<WrappingResponse> deleteWrapping(Long id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}

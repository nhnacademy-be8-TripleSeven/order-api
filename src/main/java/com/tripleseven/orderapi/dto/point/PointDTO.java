package com.tripleseven.orderapi.dto.point;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PointDTO {

    @NotNull
    int totalPoint;

    @NotNull
    int earnPoint;

    @NotNull
    int spendPoint;
}

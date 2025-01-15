package com.tripleseven.orderapi.dto.point;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonTypeName("PointDTO")
public class PointDTO {

    @NotNull
    int totalPoint;

    @NotNull
    int spendMoney;

    @NotNull
    int spendPoint;
}

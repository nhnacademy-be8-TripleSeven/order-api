package com.tripleseven.orderapi.dto.pay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO {
    private String code;
    private String message;

    public static ErrorDTO fromJson(JSONObject response) {
        ErrorDTO dto = new ErrorDTO();
        dto.setCode(response.get("code").toString());
        dto.setMessage(response.get("message").toString());
        return dto;
    }
}

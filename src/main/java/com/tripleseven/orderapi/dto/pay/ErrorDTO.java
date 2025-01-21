package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    private String code;
    private String message;

    public static ErrorDTO fromJson(JSONObject response) {
        if (response == null) {
            return new ErrorDTO("UNKNOWN_ERROR", "Unknown error occurred");
        }

        String code = response.get("code") != null ? response.get("code").toString() : "UNKNOWN_ERROR";
        String message = response.get("message") != null ? response.get("message").toString() : "Unknown error occurred";

        return new ErrorDTO(code, message);
    }
}

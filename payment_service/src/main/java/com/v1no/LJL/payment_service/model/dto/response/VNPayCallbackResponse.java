package com.v1no.LJL.payment_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayCallbackResponse {

    private Boolean success;
    private String message;
    private String orderId;
    private String redirectUrl;
}

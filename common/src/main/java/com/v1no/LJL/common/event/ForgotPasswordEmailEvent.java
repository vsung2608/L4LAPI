package com.v1no.LJL.common.event;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordEmailEvent {
    private UUID userId;
    private String email;
    private String token;
}

package com.flow.extension.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusUpdateRequest {
    private String status;

    public StatusUpdateRequest(String status) {
        this.status = status;
    }
}

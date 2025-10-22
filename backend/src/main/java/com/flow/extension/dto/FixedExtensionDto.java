package com.flow.extension.dto;

import com.flow.extension.entity.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FixedExtensionDto {
    private String name;
    private Status status;

    @Builder
    public FixedExtensionDto(String name, Status status) {
        this.name = name;
        this.status = status;
    }
}

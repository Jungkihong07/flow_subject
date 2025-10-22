package com.flow.extension.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExtensionDto {
    private String name;

    @Builder
    public ExtensionDto(String name) {
        this.name = name;
    }
}

package com.flow.extension.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExtensionCreateRequest {
    private String name;

    public ExtensionCreateRequest(String name) {
        this.name = name;
    }
}

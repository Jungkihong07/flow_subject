package com.flow.extension.entity;

import com.flow.extension.dto.ExtensionDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_extensions")
@Getter
@NoArgsConstructor
public class CustomExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Builder
    public CustomExtension(String name) {
        this.name = name;
    }

    // DTO 변환 메서드
    public ExtensionDto toDto() {
        return ExtensionDto.builder()
                .name(this.name)
                .build();
    }
}

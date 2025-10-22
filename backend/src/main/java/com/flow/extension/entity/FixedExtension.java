package com.flow.extension.entity;

import com.flow.extension.dto.FixedExtensionDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fixed_extensions")
@Getter
@NoArgsConstructor
public class FixedExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Builder
    public FixedExtension(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    // 도메인 로직: 확장자 차단 상태 변경
    public void changeBlockedStatus(Status status) {
        this.status = status;
    }

    // DTO 변환 메서드
    public FixedExtensionDto toDto() {
        return FixedExtensionDto.builder()
                .name(this.name)
                .status(this.status)
                .build();
    }
}

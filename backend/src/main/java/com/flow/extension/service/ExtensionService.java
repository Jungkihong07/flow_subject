package com.flow.extension.service;

import com.flow.extension.dto.ExtensionDto;
import com.flow.extension.dto.FixedExtensionDto;
import com.flow.extension.entity.Status;
import com.flow.extension.entity.CustomExtension;
import com.flow.extension.repository.FixedExtensionRepository;
import com.flow.extension.repository.CustomExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionService {

    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;

    public List<FixedExtensionDto> getAllFixedExtensions() {
        return fixedExtensionRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .map(entity -> entity.toDto())
                .collect(Collectors.toList());
    }

    public List<ExtensionDto> getAllCustomExtensions() {
        return customExtensionRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .map(entity -> entity.toDto())
                .collect(Collectors.toList());
    }

    @Transactional
    public FixedExtensionDto updateFixedExtensionStatus(String name, Status status) {
        return fixedExtensionRepository.findByName(name)
                .map(extension -> {
                    extension.changeBlockedStatus(status);
                    return fixedExtensionRepository.save(extension).toDto();
                })
                .orElseThrow(() -> new RuntimeException("Fixed extension not found: " + name));
    }

    @Transactional
    public ExtensionDto addCustomExtension(String name) {
        String trimmedName = name.toLowerCase().trim();

        // 고정 확장자와 중복되는지 확인
        if (fixedExtensionRepository.findByName(trimmedName).isPresent()) {
            throw new RuntimeException("Cannot add fixed extension as custom extension: " + trimmedName);
        }

        // 이미 존재하는 커스텀 확장자인지 확인
        if (customExtensionRepository.existsByName(trimmedName)) {
            throw new RuntimeException("Custom extension already exists: " + trimmedName);
        }

        CustomExtension customExtension = CustomExtension.builder()
                .name(trimmedName)
                .build();

        return customExtensionRepository.save(customExtension).toDto();
    }

    @Transactional
    public void deleteCustomExtension(String name) {
        Optional<CustomExtension> extension = customExtensionRepository.findByName(name);
        if (extension.isPresent()) {
            customExtensionRepository.delete(extension.get());
        } else {
            throw new RuntimeException("Custom extension not found: " + name);
        }
    }
}

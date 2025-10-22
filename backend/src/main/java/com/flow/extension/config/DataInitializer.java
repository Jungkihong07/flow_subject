package com.flow.extension.config;

import com.flow.extension.entity.FixedExtension;
import com.flow.extension.entity.Status;
import com.flow.extension.repository.FixedExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FixedExtensionRepository fixedExtensionRepository;

    @Override
    public void run(String... args) throws Exception {
        // 고정 확장자 초기 데이터 설정
        List<String> fixedExtensionNames = Arrays.asList("bat", "cmd", "com", "cpl", "exe", "scr", "js");

        for (String name : fixedExtensionNames) {
            if (!fixedExtensionRepository.findByName(name).isPresent()) {
                FixedExtension extension = FixedExtension.builder()
                        .name(name)
                        .status(Status.UNCHECKED)
                        .build();
                fixedExtensionRepository.save(extension);
            }
        }
    }
}

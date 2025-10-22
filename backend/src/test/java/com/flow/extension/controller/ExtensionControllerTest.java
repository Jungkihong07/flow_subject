package com.flow.extension.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.extension.config.DataInitializer;
import com.flow.extension.dto.ExtensionCreateRequest;
import com.flow.extension.dto.StatusUpdateRequest;
import com.flow.extension.entity.CustomExtension;
import com.flow.extension.entity.FixedExtension;
import com.flow.extension.entity.Status;
import com.flow.extension.repository.CustomExtensionRepository;
import com.flow.extension.repository.FixedExtensionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ExtensionController 통합 테스트")
class ExtensionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FixedExtensionRepository fixedExtensionRepository;

    @Autowired
    private CustomExtensionRepository customExtensionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() throws Exception {
        // 각 테스트 전에 DB 초기화
        customExtensionRepository.deleteAll();
        fixedExtensionRepository.deleteAll();

        // 고정 확장자 초기화
        dataInitializer.run();
    }

    @Test
    void 고정_확장자_목록_조회() throws Exception {
        // when & then - DataInitializer로 초기화된 고정 확장자들 조회
        mockMvc.perform(get("/extensions/fixed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7))) // bat, cmd, com, cpl, exe, scr, js
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].status").exists());
    }

    @Test
    void 커스텀_확장자_목록_조회() throws Exception {
        // given
        customExtensionRepository.save(
                CustomExtension.builder().name("txt").build());
        customExtensionRepository.save(
                CustomExtension.builder().name("pdf").build());

        // when & then
        mockMvc.perform(get("/extensions/custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void 고정_확장자_상태_변경() throws Exception {
        // given - DataInitializer로 초기화된 "exe" 확장자 사용
        StatusUpdateRequest request = new StatusUpdateRequest("CHECKED");

        // when
        mockMvc.perform(patch("/extensions/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("exe"))
                .andExpect(jsonPath("$.status").value("CHECKED"));

        // then - 실제 DB에서 확인
        FixedExtension updated = fixedExtensionRepository.findByName("exe").orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(Status.CHECKED);
    }

    @Test
    void 고정_확장자_없음_404() throws Exception {
        // given
        StatusUpdateRequest request = new StatusUpdateRequest("CHECKED");

        // when & then
        mockMvc.perform(patch("/extensions/fixed/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 커스텀_확장자_추가() throws Exception {
        // given
        ExtensionCreateRequest request = new ExtensionCreateRequest("txt");

        // when
        mockMvc.perform(post("/extensions/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("txt"));

        // then - 실제 DB에서 확인
        CustomExtension saved = customExtensionRepository.findByName("txt").orElseThrow();
        assertThat(saved.getName()).isEqualTo("txt");
    }

    @Test
    void 커스텀_확장자_중복_409() throws Exception {
        // given
        customExtensionRepository.save(
                CustomExtension.builder().name("txt").build());

        ExtensionCreateRequest request = new ExtensionCreateRequest("txt");

        // when & then
        mockMvc.perform(post("/extensions/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void 커스텀_확장자_빈이름_400() throws Exception {
        // given
        ExtensionCreateRequest request = new ExtensionCreateRequest("");

        // when & then
        mockMvc.perform(post("/extensions/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 커스텀_확장자_삭제() throws Exception {
        // given
        customExtensionRepository.save(
                CustomExtension.builder().name("txt").build());

        // when
        mockMvc.perform(delete("/extensions/custom/txt"))
                .andExpect(status().isOk());

        // then - 실제 DB에서 확인
        assertThat(customExtensionRepository.findByName("txt")).isEmpty();
    }

    @Test
    void 커스텀_확장자_없음_404() throws Exception {
        // when & then
        mockMvc.perform(delete("/extensions/custom/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 커스텀_확장자_대문자_소문자변환() throws Exception {
        // given
        ExtensionCreateRequest request = new ExtensionCreateRequest("TXT");

        // when
        mockMvc.perform(post("/extensions/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("txt"));

        // then - 실제 DB에서 확인
        CustomExtension saved = customExtensionRepository.findByName("txt").orElseThrow();
        assertThat(saved.getName()).isEqualTo("txt");
    }

    @Test
    void 커스텀_확장자_공백제거() throws Exception {
        // given
        ExtensionCreateRequest request = new ExtensionCreateRequest("  txt  ");

        // when
        mockMvc.perform(post("/extensions/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("txt"));

        // then - 실제 DB에서 확인
        CustomExtension saved = customExtensionRepository.findByName("txt").orElseThrow();
        assertThat(saved.getName()).isEqualTo("txt");
    }

    @Test
    void 전체_플로우_추가조회삭제() throws Exception {
        // 1. 커스텀 확장자 추가
        ExtensionCreateRequest addRequest = new ExtensionCreateRequest("txt");
        mockMvc.perform(post("/extensions/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // 2. 목록 조회로 확인
        mockMvc.perform(get("/extensions/custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("txt"));

        // 3. 삭제
        mockMvc.perform(delete("/extensions/custom/txt"))
                .andExpect(status().isOk());

        // 4. 삭제 확인
        mockMvc.perform(get("/extensions/custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

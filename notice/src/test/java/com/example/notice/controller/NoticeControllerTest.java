package com.example.notice.controller;

import com.example.notice.dto.NoticeRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        String content = objectMapper.writeValueAsString(
                NoticeRequestDto.builder()
                        .title("테스트 공지")
                        .content("테스트 공지 입니다.")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusDays(10))
                        .author("관리자")
                        .build());
        MockMultipartFile file1 = new MockMultipartFile("files", "empty.txt", "text/plain", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart("/api/v1/notices")
                        .file(file1)
                        .file(file2).headers(headers)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("POST /api/v1/notices - Create Notice")
    void createNotice() throws Exception {
        String content = objectMapper.writeValueAsString(NoticeRequestDto.builder().title("test").content("test").build());
        MockMultipartFile file1 = new MockMultipartFile("files", "empty.txt", "text/plain", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart("/api/v1/notices")
                        .file(file1)
                        .file(file2).headers(headers)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("GET /api/v1/notices - Retrieve All Notices")
    void getAllNotices() throws Exception {

        // Mock MVC 호출
        mockMvc.perform(get("/api/v1/notices")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("테스트 공지"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/notices/{id} - Retrieve Specific Notice")
    void getSpecificNotice() throws Exception {

        // Mock MVC 호출
        mockMvc.perform(get("/api/v1/notices/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 공지"))
                .andExpect(jsonPath("$.content").value("테스트 공지 입니다."))
                .andDo(print());
    }


    @Test
    @DisplayName("PUT /api/v1/notices/{id} - Update Notice")
    void updateNotice() throws Exception {
        String content = objectMapper.writeValueAsString(NoticeRequestDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .author("관리자")
                .build());
        MockMultipartFile file1 = new MockMultipartFile("files", "empty.txt", "text/plain", "test".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("requestDto", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/notices/1")
                        .file(file1)
                        .file(file2).headers(headers)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /api/v1/notices/{id} - Delete Notice")
    void deleteNotice() throws Exception {

        // Mock MVC 호출
        mockMvc.perform(delete("/api/v1/notices/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /api/v1/notices/{id} - Handle Not Found")
    void handleNoticeNotFound() throws Exception {

        // Mock MVC 호출
        mockMvc.perform(get("/api/v1/notices/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
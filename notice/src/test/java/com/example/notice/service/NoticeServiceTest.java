package com.example.notice.service;

import com.example.notice.dto.NoticeRequestDto;
import com.example.notice.dto.NoticeResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;


    @Test
    void createNotice() throws IOException {
        // Given
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .title("테스트 공지")
                .content("테스트 내용")
                .author("관리자")
                .build();

        // When
        var notice = noticeService.createNotice(dto, null);

        // Then
        assertThat(notice).isNotNull();
        assertThat(notice.getTitle()).isEqualTo("테스트 공지");
        assertThat(notice.getContent()).isEqualTo("테스트 내용");
        assertThat(notice.getAuthor()).isEqualTo("관리자");
        assertThat(notice.getStartDate()).isBefore(LocalDateTime.now().plusDays(1));
        assertThat(notice.getEndDate()).isAfter(notice.getStartDate());
    }

    /**
     * 공지사항 업데이트 테스트
     */
    @Test
    void updateNotice() throws IOException {
        // Given
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(20))
                .title("테스트 공지")
                .content("테스트 내용")
                .author("관리자")
                .build();

        // Create initial notice first
        var createdNotice = noticeService.createNotice(dto, null);

        // Update request
        NoticeRequestDto updateDto = NoticeRequestDto.builder()
                .startDate(createdNotice.getStartDate())
                .endDate(createdNotice.getEndDate().plusDays(5))
                .title("업데이트 공지 제목")
                .content("업데이트된 공지 내용")
                .author("관리자")
                .build();

        // When
        var updatedNotice = noticeService.updateNotice(createdNotice.getId(), updateDto, null);

        // Then
        assertThat(updatedNotice).isNotNull();
        assertThat(updatedNotice.getTitle()).isEqualTo("업데이트 공지 제목");
        assertThat(updatedNotice.getContent()).isEqualTo("업데이트된 공지 내용");
        assertThat(updatedNotice.getEndDate()).isAfter(createdNotice.getEndDate());
    }

    /**
     * 공지사항 전체 조회 테스트
     */
    @Test
    @Transactional
    void getAllNotices() throws IOException {
        // Given
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(20))
                .title("테스트 공지")
                .content("테스트 내용")
                .author("관리자")
                .build();

        // Create initial notice first
        var createdNotice = noticeService.createNotice(dto, null);

        // When
        List<NoticeResponseDto> notices = noticeService.getAllNotices();

        // Then
        assertThat(notices).isNotNull();
        assertThat(notices).isNotEmpty();
    }

    /**
     * 공지사항 검색 테스트
     */
    @Test
    void searchNotices() {
        // Given
        String keyword = "테스트";

        String searchStartDate = "2025-01-01";
        String searchEndDate = "2025-12-31";

        // When
        Page<NoticeResponseDto> searchedNotices = noticeService.searchNotices(keyword, searchStartDate, searchEndDate, 0, 10);

        // Then
        assertThat(searchedNotices).isNotNull();
        assertThat(searchedNotices.stream().allMatch(notice ->
                notice.getTitle().contains(keyword) || notice.getContent().contains(keyword)
        )).isTrue();
    }

    /**
     * 공지사항 ID로 단건 조회 테스트
     */
    @Test
    @Transactional
    void getNoticeById() throws IOException {
        // Given
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .title("조회 테스트 공지")
                .content("조회 테스트 내용")
                .author("관리자")
                .build();

        var createdNotice = noticeService.createNotice(dto, null);

        // When
        var foundNotice = noticeService.getNoticeById(createdNotice.getId());

        // Then
        assertThat(foundNotice).isNotNull();
        assertThat(foundNotice.getId()).isEqualTo(createdNotice.getId());
        assertThat(foundNotice.getTitle()).isEqualTo(createdNotice.getTitle());
    }

    /**
     * 공지사항 삭제 테스트
     */
    @Test
    void deleteNotice() throws IOException {
        // Given
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .title("삭제 테스트 공지")
                .content("삭제 테스트 내용")
                .author("관리자")
                .build();

        var createdNotice = noticeService.createNotice(dto, null);

        // When
        noticeService.deleteNotice(createdNotice.getId());

        // Then
        assertThrows(RuntimeException.class, () -> noticeService.getNoticeById(createdNotice.getId()));
    }
}


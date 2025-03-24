package com.example.notice.dto;

import com.example.notice.entity.Notice;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class NoticeResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int viewCount;
    private String author;
    private List<String> attachmentUrls;

}

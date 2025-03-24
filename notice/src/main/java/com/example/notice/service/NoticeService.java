package com.example.notice.service;

import com.example.notice.dto.NoticeRequestDto;
import com.example.notice.dto.NoticeResponseDto;
import com.example.notice.entity.Attachment;
import com.example.notice.entity.Notice;
import com.example.notice.exception.NotFoundException;
import com.example.notice.repository.AttachmentRepository;
import com.example.notice.repository.NoticeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final AttachmentRepository attachmentRepository;

    private final FileService fileService;


    public NoticeService(NoticeRepository noticeRepository, AttachmentRepository attachmentRepository, FileService fileService) {
        this.noticeRepository = noticeRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileService = fileService;
    }

    @CacheEvict(value = "notices", allEntries = true)
    @Transactional
    public NoticeResponseDto createNotice(NoticeRequestDto dto, List<MultipartFile> files) throws IOException {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .author(dto.getAuthor())
                .createdAt(java.time.LocalDateTime.now())
                .viewCount(0)
                .build();

        if (files != null) {
            List<Attachment> attachments = fileService.saveFiles(files);

            for (Attachment attachment : attachments) {
                attachment.setNotice(notice);
                attachmentRepository.save(attachment);
            }
            notice.setAttachments(attachments);
        }

        noticeRepository.save(notice);

        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor())
                .createdAt(notice.getCreatedAt())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .viewCount(notice.getViewCount())
                .attachmentUrls(Optional.ofNullable(notice.getAttachments()).orElseGet(ArrayList::new)
                        .stream().map(Attachment::getFileUrl).toList())
                .build();
    }

    @CacheEvict(value = "notices", allEntries = true)
    @Transactional
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto dto, List<MultipartFile> files) throws IOException {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(dto.getTitle() != null ? dto.getTitle() : notice.getTitle());
        notice.setContent(dto.getContent() != null ? dto.getContent() : notice.getContent());
        notice.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : notice.getStartDate());
        notice.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : notice.getEndDate());
        notice.setAuthor(dto.getAuthor() != null ? dto.getAuthor() : notice.getAuthor());

        // Clear existing attachments
        notice.getAttachments().clear();

        if (files != null) {
            attachmentRepository.deleteAll(notice.getAttachments());

            notice.getAttachments().clear();

            // Save new attachments if provided
            List<Attachment> attachments = fileService.saveFiles(files);
            for (Attachment attachment : attachments) {
                attachment.setNotice(notice);
            }
            notice.getAttachments().addAll(attachments);
        }

        // Save the updated notice
        noticeRepository.save(notice);

        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor())
                .createdAt(notice.getCreatedAt())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .viewCount(notice.getViewCount())
                .attachmentUrls(Optional.ofNullable(notice.getAttachments()).orElseGet(ArrayList::new)
                        .stream().map(Attachment::getFileUrl).toList())
                .build();
    }

    public List<NoticeResponseDto> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(notice -> NoticeResponseDto.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .author(notice.getAuthor())
                        .createdAt(notice.getCreatedAt())
                        .startDate(notice.getStartDate())
                        .endDate(notice.getEndDate())
                        .viewCount(notice.getViewCount())
                        .attachmentUrls(Optional.ofNullable(notice.getAttachments()).orElseGet(ArrayList::new)
                                .stream().map(Attachment::getFileUrl).toList())
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable(value = "notices", key = "#keyword + '-' + #page + '-' + #size")
    public Page<NoticeResponseDto> searchNotices(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // 페이지 및 크기 설정

        Page<Notice> noticePage = noticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        return noticePage.map(notice -> NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor())
                .createdAt(notice.getCreatedAt())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .viewCount(notice.getViewCount())
                .attachmentUrls(Optional.ofNullable(notice.getAttachments()).orElseGet(ArrayList::new)
                        .stream().map(Attachment::getFileUrl).toList())
                .build());
    }

    @Cacheable(value = "notice", key = "#id")
    public NoticeResponseDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .author(notice.getAuthor())
                .createdAt(notice.getCreatedAt())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .viewCount(notice.getViewCount())
                .attachmentUrls(Optional.ofNullable(notice.getAttachments()).orElseGet(ArrayList::new)
                        .stream().map(Attachment::getFileUrl).toList())
                .build();
    }

    @CacheEvict(value = "notices", allEntries = true)
    @Transactional
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}

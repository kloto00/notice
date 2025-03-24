package com.example.notice.controller;

import com.example.notice.dto.NoticeRequestDto;
import com.example.notice.dto.NoticeResponseDto;
import com.example.notice.exception.NotFoundException;
import com.example.notice.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping(value="/notices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponseDto> createNotice(@RequestPart NoticeRequestDto requestDto,
                                                          @RequestPart(required = false) List<MultipartFile> files) throws IOException {
        return ResponseEntity.ok(noticeService.createNotice(requestDto, files));
    }

    @PutMapping(value="/notices/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long id,
                                                          @RequestPart NoticeRequestDto requestDto,
                                                          @RequestPart(required = false) List<MultipartFile> files) throws IOException {
        try{
            return ResponseEntity.ok(noticeService.updateNotice(id, requestDto, files));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponseDto>> getNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @GetMapping("/notices/search")
    public Page<NoticeResponseDto> searchNotices(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return noticeService.searchNotices(keyword, page, size);
    }


    @GetMapping("/notices/{id}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long id) {

        try{
            return ResponseEntity.ok(noticeService.getNoticeById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        try{
            noticeService.deleteNotice(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }
}

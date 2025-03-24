package com.example.notice.service;

import com.example.notice.entity.Attachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private static final String UPLOAD_DIR = "d:/uploads/notice"; // 파일 저장 경로
    private static final String FileUrl = "localhost:8080/notice/download";

    public List<Attachment> saveFiles(List<MultipartFile> files) throws IOException {
        List<Attachment> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destinationFile = new File(UPLOAD_DIR, uniqueFileName);
                file.transferTo(destinationFile); // 파일 저장
                attachments.add(Attachment.builder()
                        .originalFileName(file.getName())
                        .fileName(uniqueFileName)
                        .filePath(destinationFile.getAbsolutePath())
                        .fileUrl(FileUrl + "/" + uniqueFileName)
                        .build());
            }
        }

        return attachments; // Attachment 엔티티 목록 반환
    }
}
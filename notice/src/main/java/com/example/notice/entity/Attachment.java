package com.example.notice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String fileName;
    private String filePath;
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;
}

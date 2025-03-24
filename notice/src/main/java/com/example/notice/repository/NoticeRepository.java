package com.example.notice.repository;

import com.example.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

}

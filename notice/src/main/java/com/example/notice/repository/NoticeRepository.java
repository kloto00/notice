package com.example.notice.repository;

import com.example.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByCreatedAtBetweenAndTitleContainingOrContentContaining(LocalDateTime startDate, LocalDateTime endDate, String titleKeyword, String contentKeyword, Pageable pageable);

    @Query("SELECT n FROM Notice n " +
            "WHERE n.createdAt BETWEEN :startDate AND :endDate " +
            "AND (LOWER(cast(n.title as string)) LIKE LOWER(CONCAT('%', cast(:titleKeyword as string), '%')) " +
            "OR LOWER(cast(n.content as string)) LIKE LOWER(CONCAT('%', cast(:contentKeyword as string), '%')))"
    )
    Page<Notice> searchNotice(LocalDateTime startDate, LocalDateTime endDate, String titleKeyword, String contentKeyword, Pageable pageable);

}

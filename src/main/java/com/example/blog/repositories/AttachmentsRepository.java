package com.example.blog.repositories;

import com.example.blog.domain.Attachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentsRepository extends JpaRepository<Attachments, Integer> {
    void deleteAllByPostsId(int id);
}

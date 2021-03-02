package com.example.blog.repositories;

import com.example.blog.domain.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    void deleteAllByPostsId(int id);
}

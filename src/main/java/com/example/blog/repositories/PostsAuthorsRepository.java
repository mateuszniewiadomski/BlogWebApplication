package com.example.blog.repositories;

import com.example.blog.domain.PostsAuthors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsAuthorsRepository extends JpaRepository<PostsAuthors, Integer> {
    @Modifying
    @Query(
            "delete from PostsAuthors p where p.id_post.id=:id"
    )
    void deletePostsAuthors(@Param("id") int id);
}

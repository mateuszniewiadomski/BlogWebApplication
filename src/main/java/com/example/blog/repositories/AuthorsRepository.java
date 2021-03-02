package com.example.blog.repositories;

import com.example.blog.domain.Authors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorsRepository extends JpaRepository<Authors, Integer> {
    Authors findByUsername(String username);
}

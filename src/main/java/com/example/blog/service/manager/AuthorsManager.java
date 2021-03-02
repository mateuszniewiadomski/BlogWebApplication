package com.example.blog.service.manager;

import com.example.blog.domain.Authors;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AuthorsManager extends UserDetailsService {

    void save(Authors authors);

    void saveAll();

    Authors findById(int id);

    Authors findByUsername(String username);

    List<Authors> getAllAuthors();

}

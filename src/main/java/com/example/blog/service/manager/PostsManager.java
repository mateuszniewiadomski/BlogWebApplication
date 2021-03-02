package com.example.blog.service.manager;

import com.example.blog.domain.Posts;

import java.util.List;
import java.util.Map;

public interface PostsManager {

    void save(Posts post);

    Posts findById(int id);

    List<Posts> getAllPosts();

    List<Posts> getAllPostsOrdered(int order, Map<Integer, Integer> comments, Map<Integer, Integer> attachments);

    List<Posts> getFilteredPosts(String filter);

    List<Posts> getAllPostsByTag(String tag);

    void remove(int id);

}

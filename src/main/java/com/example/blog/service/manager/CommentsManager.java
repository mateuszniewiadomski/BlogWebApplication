package com.example.blog.service.manager;

import com.example.blog.domain.Comments;

import java.util.List;
import java.util.Map;

public interface CommentsManager {

    void save(Comments comment);

    Comments findById(int id);

    List<Comments> getAllComments();

    void remove(int id);

    void removeByPostId(int id);

    Map<Integer, Integer> getCommentsWithAmount();
}

package com.example.blog.service;

import com.example.blog.domain.Comments;
import com.example.blog.domain.Posts;
import com.example.blog.repositories.CommentsRepository;
import com.example.blog.repositories.PostsRepository;
import com.example.blog.service.manager.CommentsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@DependsOn("postsInMemoryService")
public class CommentsInMemoryService implements CommentsManager {

    private final List<Comments> commentsList;
    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;

    @Autowired
    public CommentsInMemoryService(CommentsRepository commentsRepository, List<Comments> commentsList, PostsRepository postsRepository) {
        this.commentsRepository = commentsRepository;
        this.commentsList = commentsList;
        this.postsRepository = postsRepository;
    }

    @Override
    public void save(Comments comment) {
        comment.setPosts(postsRepository.findById(comment.getId_post()).get());
        commentsRepository.save(comment);
    }

    @Override
    public Comments findById(int id) { return commentsRepository.findById(id).get(); }

    @Override
    public List<Comments> getAllComments() { return commentsRepository.findAll(); }

    @Override
    public void remove(int id) { commentsRepository.deleteById(id); }

    @Override
    public void removeByPostId(int id) { commentsRepository.deleteAllByPostsId(id);
         }

    @Override
    public Map<Integer, Integer> getCommentsWithAmount() {
        List<Integer> integerList = new ArrayList<Integer>();
        for (Comments comments : getAllComments()) {
            integerList.add(comments.getId_post());
        }
        for (Posts post : postsRepository.findAll()) {
            integerList.add(post.getId());
        }
        Map<Integer, Integer> countMap = new HashMap<>();

        for (Integer item : integerList) {

            if (countMap.containsKey(item))
                countMap.put(item, countMap.get(item) + 1);
            else
                countMap.put(item, 1);
        }
        return countMap;
    }

    public void saveAll() {
        commentsRepository.saveAll(commentsList);
    }

    public void savePosts() {

        for (Comments c : commentsList) {
            c.setPosts(postsRepository.findById(c.getId_post()).get());
        }

    }


    @PostConstruct
    public void init() {
        savePosts();
        saveAll();
    }
}

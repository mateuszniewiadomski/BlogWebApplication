package com.example.blog.service;

import com.example.blog.domain.Posts;
import com.example.blog.repositories.PostsRepository;
import com.example.blog.service.manager.PostsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostsInMemoryService implements PostsManager {

    private final List<Posts> postsList;
    private final PostsRepository postsRepository;

    @Autowired
    public PostsInMemoryService(PostsRepository postsRepository, List<Posts> postsList) {
        this.postsRepository = postsRepository;
        this.postsList = postsList;
    }

    @Override
    public void save(Posts post) { postsRepository.save(post); }

    @Override
    public Posts findById(int id) { return postsRepository.findById(id).get(); }

    @Override
    public List<Posts> getAllPosts() { return postsRepository.findAll(); }

    @Override
    public List<Posts> getAllPostsOrdered(int order, Map<Integer, Integer> comments, Map<Integer, Integer> attachments) {
        List<Posts> posts = postsRepository.findAll();
        List<Posts> list = new ArrayList<>();
        List<Integer> sorted;
        switch (order) {
            case 1:
                return posts;
            case 2:
                sorted = comments.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                for (Integer id : sorted) {
                    for (Posts post : posts) {
                        if (id == post.getId()) list.add(post);
                    }
                }
                return list;
            case 3:
                sorted = comments.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                for (Integer id : sorted) {
                    for (Posts post : posts) {
                        if (id == post.getId()) list.add(post);
                    }
                }
                return list;
            case 4:
                sorted = attachments.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                for (Integer id : sorted) {
                    for (Posts post : posts) {
                        if (id == post.getId()) list.add(post);
                    }
                }
                return list;
            case 5:
                sorted = attachments.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                for (Integer id : sorted) {
                    for (Posts post : posts) {
                        if (id == post.getId()) list.add(post);
                    }
                }
                return list;
            default:
                posts.sort(Comparator.comparing(Posts::getId).reversed());
                return posts;
        }
    }

    @Override
    public List<Posts> getFilteredPosts(String filter) {
        List<Posts> posts = new ArrayList<>();
        for (Posts post : postsRepository.findAll()) {
            if(post.getTags().contains(filter)
            || post.getPost_content().contains(filter)) posts.add(post);
        }
        posts.sort(Comparator.comparing(Posts::getId).reversed());
        return posts;
    }

    @Override
    public List<Posts> getAllPostsByTag(String tag) {
        List<Posts> posts = new ArrayList<>();
        for (Posts post : postsRepository.findAll()) {
            if(post.getTags().contains(tag)) posts.add(post);
        }
        posts.sort(Comparator.comparing(Posts::getId).reversed());
        return posts;
    }

    @Override
    public void remove(int id) { postsRepository.deleteById(id); }

    public void saveAll() {
        postsRepository.saveAll(postsList);
    }

    @PostConstruct
    public void init() {
        saveAll();
    }
}

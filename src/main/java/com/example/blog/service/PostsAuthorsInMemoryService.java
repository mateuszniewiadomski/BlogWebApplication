package com.example.blog.service;

import com.example.blog.domain.Authors;
import com.example.blog.domain.Posts;
import com.example.blog.domain.PostsAuthors;
import com.example.blog.domain.PostsAuthorsId;
import com.example.blog.repositories.AuthorsRepository;
import com.example.blog.repositories.PostsAuthorsRepository;
import com.example.blog.repositories.PostsRepository;
import com.example.blog.service.manager.PostsAuthorsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostsAuthorsInMemoryService implements PostsAuthorsManager {

    private final List<PostsAuthorsId> postsAuthorsList;
    private final PostsAuthorsRepository postsAuthorsRepository;
    private final AuthorsRepository authorsRepository;
    private final PostsRepository postsRepository;

    @Autowired
    public PostsAuthorsInMemoryService(AuthorsRepository authorsRepository, PostsRepository postsRepository, PostsAuthorsRepository postsAuthorsRepository, List<PostsAuthorsId> postsAuthorsList, PostsInMemoryService postsInMemoryService, AuthorsInMemoryService authorsInMemoryService) {
        this.authorsRepository = authorsRepository;
        this.postsRepository = postsRepository;
        this.postsAuthorsRepository = postsAuthorsRepository;
        this.postsAuthorsList = postsAuthorsList;
    }

    @Override
    public void save(int id_post, int id_author) {
        PostsAuthorsId postsAuthorsId = new PostsAuthorsId(id_post, id_author);
        PostsAuthors postsAuthors = new PostsAuthors(postsAuthorsId, postsRepository.findById(id_post).get(), authorsRepository.findById(id_author).get());
        postsAuthorsRepository.save(postsAuthors);
    }

    @Override
    public void removeByPost(int id_post) { postsAuthorsRepository.deletePostsAuthors(id_post); }

    @Override
    public List<PostsAuthors> getAllPostsAuthors() {
        return postsAuthorsRepository.findAll();
    }

    @Override
    public List<Posts> getAllPostsByAuthors(int id_author) {
        List<Posts> postsList = new ArrayList<>();
        for (PostsAuthors pa : postsAuthorsRepository.findAll()) {
            if (pa.getId_author().getId() == id_author) postsList.add(postsRepository.findById(pa.getId_post().getId()).get());
        }
        return postsList;
    }

    public void saveAll() {
        for (PostsAuthorsId p : postsAuthorsList) {
            Posts post = postsRepository.findById(p.getId_post()).get();
            Authors authors = authorsRepository.findById(p.getId_author()).get();
            postsAuthorsRepository.saveAndFlush(new PostsAuthors(p, post, authors));
        }
    }

    @PostConstruct
    public void init() {
        saveAll();
    }

}

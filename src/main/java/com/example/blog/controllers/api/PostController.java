package com.example.blog.controllers.api;

import com.example.blog.domain.*;
import com.example.blog.service.manager.*;
import com.example.blog.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PostController {
    private final AuthorsManager authorsManager;
    private final PostsManager postsManager;
    private final PostsAuthorsManager postsAuthorsManager;
    private final CommentsManager commentsManager;
    private final AttachmentsManager attachmentsManager;
    private final StorageService storageService;

    @Autowired
    public PostController(AuthorsManager authorsManager, PostsManager postsManager, PostsAuthorsManager postsAuthorsManager, CommentsManager commentsManager, AttachmentsManager attachmentsManager, StorageService storageService) {
        this.authorsManager = authorsManager;
        this.postsManager = postsManager;
        this.postsAuthorsManager = postsAuthorsManager;
        this.commentsManager = commentsManager;
        this.attachmentsManager = attachmentsManager;
        this.storageService = storageService;
    }

    @PostMapping("api/register")
    public String registerUser(@RequestBody Authors author) {
        authorsManager.save(author);
        return "Successfully user added!";
    }

    @PostMapping("api/post")
    public String addNewPost(@RequestBody Posts posts) {
        Authors author = authorsManager.findByUsername("admin");
        posts.replaceCommas(posts.getTags());
        postsManager.save(posts);
        postsAuthorsManager.save(posts.getId(), author.getId());
        return "Added new post successfully!";
    }

    @PutMapping("api/tag")
    public String addAttachments(@RequestBody Posts posts) {
        Posts currPost = postsManager.findById(posts.getId());
        if (currPost == null) return "There is no post with such ID";
        posts.replaceCommas(posts.getTags());
        currPost.setTags(currPost.getTags() + " " + posts.getTags());
        postsManager.save(currPost);
        return "Tags added!";
    }

    @DeleteMapping("api/post")
    public String deletePost(@RequestBody int id) {
        attachmentsManager.removeByPostId(id);
        commentsManager.removeByPostId(id);
        postsAuthorsManager.removeByPost(id);
        postsManager.remove(id);
        return "Post deleted successfully!";
    }

    @PutMapping("api/post")
    public String updatePost(@RequestBody Posts posts) {
        Posts currPost = postsManager.findById(posts.getId());
        currPost.setPost_content(posts.getPost_content());
        postsManager.save(currPost);
        return "Post content updated!";
    }

    /*Postman -> body -> form-data -> {id type:Text, files type: File}*/
    @PostMapping("api/attachment")
    public String addAttachment(@RequestParam("id") int id, @RequestParam("files") MultipartFile[] files) {
        Posts post = postsManager.findById(id);
        storageService.store(files);
        attachmentsManager.save(files, post.getId());
        return "Attachment added successfully!";
    }

    @DeleteMapping("api/attachment")
    public String deleteAttachment(@RequestBody int id) {
        attachmentsManager.remove(id);
        return "Attachment deleted successfully";
    }

    /*Postman -> save response}*/
    @GetMapping("api/attachment/{id}")
    public Resource downloadAttachment(@PathVariable("id") int id) {
        String filename = attachmentsManager.getAttachmentFilename(id);
        return storageService.loadAsResource(filename);
    }

    @PostMapping("api/comment")
    public String addComment(@RequestBody Comments comment) {
        comment.setUsername("admin");
        commentsManager.save(comment);
        return "Comment added successfully";
    }

    @PutMapping("api/comment")
    public String updateComment(@RequestBody Comments comment) {
        Comments c = commentsManager.findById(comment.getId());
        c.setComment_content(comment.getComment_content());
        commentsManager.save(c);
        return "Comment updated successfully";
    }

    @DeleteMapping("api/comment")
    public String deleteComment(@RequestBody int id) {
        commentsManager.remove(id);
        return "Comment deleted successfully";
    }
}

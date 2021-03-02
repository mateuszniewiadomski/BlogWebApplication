package com.example.blog.controllers.web;

import com.example.blog.domain.Comments;
import com.example.blog.domain.Posts;
import com.example.blog.service.manager.*;
import com.example.blog.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller("postwebcontroller")
public class PostController {

    private final PostsManager postsManager;
    private final PostsAuthorsManager postsAuthorsManager;
    private final CommentsManager commentsManager;
    private final AttachmentsManager attachmentsManager;
    private final StorageService storageService;

    @Autowired
    public PostController(PostsManager postsManager, PostsAuthorsManager postsAuthorsManager, CommentsManager commentsManager, AttachmentsManager attachmentsManager, StorageService storageService) {
        this.postsManager = postsManager;
        this.postsAuthorsManager = postsAuthorsManager;
        this.commentsManager = commentsManager;
        this.attachmentsManager = attachmentsManager;
        this.storageService = storageService;
    }

    //view main post window
    @GetMapping("/post/{id}")
    public String post(@PathVariable("id") int id, Model model) {
        Posts post = postsManager.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("authors", post.getPostsAuthors().stream().map(e -> e.getId_author().getUsername()).collect(Collectors.toList()));
        model.addAttribute("tags", Arrays.asList(post.getTags().split(" ")));
        model.addAttribute("files", storageService.loadAll()
                .filter(c -> c.getFileName().toString().matches("(.*[^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)"))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList()));
        model.addAttribute("comments", new Comments());
        return "post";
    }

    //add comment
    @PostMapping("/post/{post_id}/add_comment")
    public String addComment(@PathVariable("post_id") int post_id, @Valid @ModelAttribute("comment") Comments comments, BindingResult br, String username, int id_post, Model model) {
        if (br.hasErrors()) {
            Posts post = postsManager.findById(post_id);
            model.addAttribute("post", post);
            model.addAttribute("authors", post.getPostsAuthors().stream().map(e -> e.getId_author().getUsername()).collect(Collectors.toList()));
            model.addAttribute("tags", Arrays.asList(post.getTags().split(" ")));
            model.addAttribute("files", storageService.loadAll()
                    .filter(c -> c.getFileName().toString().matches("(.*[^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)"))
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList()));
            model.addAttribute("comments", comments);
            return "post";
        }
        comments.setUsername(username);
        commentsManager.save(comments);
        return "redirect:/post/" + post_id;
    }

    //delete post
    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable("id") int id) {
        attachmentsManager.removeByPostId(id);
        commentsManager.removeByPostId(id);
        postsAuthorsManager.removeByPost(id);
        postsManager.remove(id);
        return "redirect:/";
    }

    //delete comment
    @GetMapping("/post/{id}/comment/delete/{commId}")
    public String deleteComment(@PathVariable int id, @PathVariable int commId) {
        commentsManager.remove(commId);
        return "redirect:/post/"+id;
    }

    //delete attachment
    @GetMapping("/post/{id}/attachment/delete/{attId}")
    public String deleteAttachment(@PathVariable int id, @PathVariable int attId) {
        attachmentsManager.remove(attId);
        return "redirect:/post/edit/"+id;
    }

    //edit post
    @GetMapping("/post/edit/{id}")
    public String editPost(Model model, @PathVariable int id) {
        List<String> tagsWithDuplication = new ArrayList<>();
        postsManager.getAllPosts().forEach(tags -> tagsWithDuplication.addAll(Arrays.asList(tags.getTags().split(" "))));
        List<String> allTags = new ArrayList<>(new HashSet<>(tagsWithDuplication));
        Collections.sort(allTags);
        Posts post = postsManager.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("allTags", allTags);
        model.addAttribute("attachments", post.getAttachments());
        model.addAttribute("PostId", id);

        return "edit-post";
    }

    //update post
    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable int id, @Valid @ModelAttribute Posts post, BindingResult br, @RequestParam("files") MultipartFile[] files) {
        if(br.hasErrors()) {
            return "edit-post";
        }
        post.replaceCommas(post.getTags());
        postsManager.save(post);
        storageService.store(files);
        attachmentsManager.save(files, post.getId());
        return "redirect:/post/"+id;
    }

    //edit comment
    @GetMapping("/post/{id}/comment/{cId}")
    public String editComment(Model model, @PathVariable int id, @PathVariable int cId) {
        model.addAttribute("comment", commentsManager.findById(cId));
        return "post-comment-edit";
    }

    @PostMapping("/post/{id}/comment/{commId}")
    public String updatePost(@PathVariable int id, @PathVariable int commId, @Valid Comments comment, BindingResult br) {
        if (br.hasErrors()) {
            return "post-comment-edit";
        }
        comment.setId_post(id);
        comment.setId(commId);
        comment.setUsername(commentsManager.findById(commId).getUsername());
        commentsManager.save(comment);
        return "redirect:/post/" + id;
    }


}

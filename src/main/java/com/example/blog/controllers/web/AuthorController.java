package com.example.blog.controllers.web;

import com.example.blog.domain.Comments;
import com.example.blog.domain.Posts;
import com.example.blog.service.manager.*;
import com.example.blog.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller("authorwebcontroller")
public class AuthorController {

    private final AuthorsManager authorsManager;
    private final PostsAuthorsManager postsAuthorsManager;
    private final CommentsManager commentsManager;

    @Autowired
    public AuthorController(AuthorsManager authorsManager, PostsAuthorsManager postsAuthorsManager, CommentsManager commentsManager) {
        this.authorsManager = authorsManager;
        this.postsAuthorsManager = postsAuthorsManager;
        this.commentsManager = commentsManager;
    }

    @GetMapping("/author/{id}")
    public String authorWindow(@PathVariable("id") int id, Model model) {
        List<Posts> posts = postsAuthorsManager.getAllPostsByAuthors(authorsManager.findById(id).getId());
        model.addAttribute("author", authorsManager.findById(id));
        model.addAttribute("posts", posts);
        model.addAttribute("comments", new Comments());
        return "author";
    }

    //add comment
    @PostMapping("/author/{id}/add_comment")
    public String addComment(@PathVariable("id") int id, @Valid @ModelAttribute("comment") Comments comment, BindingResult br, String username, int id_post, Model model) {
        if (br.hasErrors()) {
            List<Posts> posts = postsAuthorsManager.getAllPostsByAuthors(authorsManager.findById(id).getId());
            model.addAttribute("author", authorsManager.findById(id));
            model.addAttribute("posts", posts);
            model.addAttribute("comments", comment);
            return "author";
        }
        comment.setUsername(username);
        commentsManager.save(comment);
        return "redirect:/author/" + id;
    }

}

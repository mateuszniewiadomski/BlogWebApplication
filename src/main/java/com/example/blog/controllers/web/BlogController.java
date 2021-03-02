package com.example.blog.controllers.web;

import com.example.blog.domain.Attachments;
import com.example.blog.domain.Authors;
import com.example.blog.domain.Comments;
import com.example.blog.domain.Posts;
import com.example.blog.service.manager.*;
import com.example.blog.storage.StorageFileNotFoundException;
import com.example.blog.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BlogController {

    private final AuthorsManager authorsManager;
    private final PostsManager postsManager;
    private final PostsAuthorsManager postsAuthorsManager;
    private final CommentsManager commentsManager;
    private final AttachmentsManager attachmentsManager;
    private final StorageService storageService;

    @Autowired
    public BlogController(AuthorsManager authorsManager, PostsManager postsManager, PostsAuthorsManager postsAuthorsManager, CommentsManager commentsManager, AttachmentsManager attachmentsManager, StorageService storageService) {
        this.authorsManager = authorsManager;
        this.postsManager = postsManager;
        this.postsAuthorsManager = postsAuthorsManager;
        this.commentsManager = commentsManager;
        this.attachmentsManager = attachmentsManager;
        this.storageService = storageService;
    }

    //main window
    @GetMapping("/")
    public String home(Model model) {
        setAllAttributes(model);
        return "home";
    }

    //search posts
    @RequestMapping("/search")
    public String filter(@RequestParam String filter, Model model) {
        setAllAttributes(model);
        model.addAttribute("posts", postsManager.getFilteredPosts(filter));
        return "home";
    }

    //search by tag
    @GetMapping("/search/{id}")
    public String filterByTag(@PathVariable("id") String id, Model model) {
        setAllAttributes(model);
        model.addAttribute("posts", postsManager.getAllPostsByTag(id));
        return "home";
    }

    //sort posts
    @GetMapping("/sort/{id}")
    public String sortBy(@PathVariable("id") int id, Model model) {
        setAllAttributes(model);
        model.addAttribute("posts", postsManager.getAllPostsOrdered(id, commentsManager.getCommentsWithAmount(), attachmentsManager.getAttachmentsWithAmount()));
        return "home";
    }

    private void setAllAttributes(Model model) {

        List<String> tagsWithDuplication = new ArrayList<>();
        postsManager.getAllPosts().forEach(tags -> tagsWithDuplication.addAll(Arrays.asList(tags.getTags().split(" "))));
        List<String> allTags = new ArrayList<>(new HashSet<>(tagsWithDuplication));
        Collections.sort(allTags);

        //add attributes
        //ADD POST
        model.addAttribute("post", new Posts());
        model.addAttribute("allTags", allTags);
        model.addAttribute("attachments", new ArrayList<Attachments>());
        //FILTERS / SORTING

        //POSTS
        model.addAttribute("posts", postsManager.getAllPostsOrdered(-1, null, null));
        model.addAttribute("files", storageService.loadAll()
                .filter(c -> c.getFileName().toString().matches("(.*[^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)"))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList()));
        model.addAttribute("search","");
        model.addAttribute("comments", new Comments());

        //Generates CSV files
        storageService.generateCSV();
    }


    @PostMapping("/add_post")
    public String addPost(@Valid Posts posts, BindingResult br, @CurrentSecurityContext(expression="authentication.name") String username, BindingResult br2, @RequestParam("files") MultipartFile[] files) {
        if (br.hasErrors() || br2.hasErrors()) {
            return "home";
        }
        Authors author = authorsManager.findByUsername(username);
        posts.replaceCommas(posts.getTags());
        postsManager.save(posts);
        postsAuthorsManager.save(posts.getId(), author.getId());
        storageService.store(files);
        attachmentsManager.save(files, posts.getId());

        return "redirect:/";
    }

    //add comment
    @PostMapping("/add_comment")
    public String addComment(@Valid @ModelAttribute("comment") Comments comment, BindingResult br, String username, int id_post, Model model) {
        if (br.hasErrors()) {
            return "home";
        }
        comment.setUsername(username);
        commentsManager.save(comment);
        return "redirect:/";
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    public Authentication getCurrentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
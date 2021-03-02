package com.example.blog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Entity
@Table(name = "posts")
public class Posts {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Length(min = 1, message = "Content of the Post should be start at least one character")
    @Column(columnDefinition = "TEXT", name = "postContent")
    private String post_content;

    @Size(min = 1, message = "Tags are required")
    private String tags;

    public void replaceCommas(String tags) {
        this.tags = tags.replaceAll(",", " ").replaceAll(" +", " ");
    }

    @OneToMany(mappedBy = "id_post", cascade = {CascadeType.MERGE})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<PostsAuthors> postsAuthors;

    @OneToMany(mappedBy = "posts", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Comments> comments;

    @OneToMany(mappedBy = "posts", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Attachments> attachments;
}

package com.example.blog.domain;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts_authors")
public class PostsAuthors {

    @EmbeddedId
    private PostsAuthorsId id;

    @NonNull
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Posts id_post;

    @NonNull
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Authors id_author;

    public PostsAuthors(Posts id_post, Authors id_author) {
        this.id_post = id_post;
        this.id_author = id_author;
    }
}

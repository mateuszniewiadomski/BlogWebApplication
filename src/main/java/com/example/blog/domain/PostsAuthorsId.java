package com.example.blog.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PostsAuthorsId implements Serializable {

    @NonNull
    @Column(name = "id_post")
    @Getter
    @Setter
    private int id_post;

    @NonNull
    @Column(name = "id_author")
    @Getter @Setter
    private int id_author;

}

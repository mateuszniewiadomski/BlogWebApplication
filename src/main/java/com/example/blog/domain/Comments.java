package com.example.blog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private int id_post;

    @NotNull(message = "Content of the comment is required")
    @Size(min = 1, message = "Content of the comment should be start at least one character")
    @Column(columnDefinition = "TEXT")
    private String comment_content;

    @ManyToOne
    private Posts posts;
}

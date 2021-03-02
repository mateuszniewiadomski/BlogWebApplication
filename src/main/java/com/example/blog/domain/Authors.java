package com.example.blog.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authors",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class Authors {

    @NonNull
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    @NotNull(message = "Name is required")
    @Size(min = 2, message = "Name should be start at least two characters")
    private String first_name;

    @NonNull
    @NotNull(message = "Lastname is required")
    @Size(min = 2, message = "Lastname should be start at least two characters")
    private String last_name;

    @NonNull
    @NotNull(message = "Username is required")
    @Size(min = 2, message = "Username should be start at least two characters")
    private String username;

    @Email
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Roles> roles;

    @OneToMany(mappedBy = "id_author", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<PostsAuthors> postsAuthors;

    public Authors(String first_name, String last_name, String username, String email, String password, Collection<Roles> roles) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public Authors(String first_name, String last_name, String username, String email, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

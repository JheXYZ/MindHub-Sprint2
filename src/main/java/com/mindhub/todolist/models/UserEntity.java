package com.mindhub.todolist.models;

import com.mindhub.todolist.dtos.user.NewUserRequestDTO;
import com.mindhub.todolist.validations.NoWhitespaces;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "username must not be empty")
    @NoWhitespaces(message = "username can not contain whitespaces")
    private String username;

    @NotBlank(message = "password must not be empty")
    @Column(nullable = false)
    @NoWhitespaces(message = "password can not contain whitespaces")
    private String password;

    @NotBlank(message = "email must not be empty")
    @Email(message = "invalid email")
    @Column(nullable = false, unique = true)
    @NoWhitespaces(message = "username can not contain whitespaces")
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Task> tasks = new HashSet<>();

    private UserAuthority authority = UserAuthority.USER;

    public UserEntity() {
    }

    public UserEntity(long id, String email, String password, String username, UserAuthority authority) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.authority = authority;
    }

    public UserEntity(String email, String password, String username, UserAuthority authority) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.authority = authority;
    }


    public UserEntity(NewUserRequestDTO newUserRequestDTO) {
        this.email = newUserRequestDTO.email();
        this.password = newUserRequestDTO.password();
        this.username = newUserRequestDTO.username();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public UserAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(UserAuthority authority) {
        this.authority = authority;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        task.setUser(this);
        this.tasks.add(task);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", tasks=" + tasks +
                '}';
    }

    public String toStringWithoutTasks() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

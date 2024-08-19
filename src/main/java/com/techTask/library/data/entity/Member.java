package com.techTask.library.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Size(min = 1, max = 50)
    @Column(unique = true)
    private String name;
    private LocalDateTime membership_date;

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    private Set<Book> books = new HashSet<Book>();

    public Member() {
    }

    public Member(long id, String name, LocalDateTime membership_date, Set<Book> books) {
        this.id = id;
        this.name = name;
        this.membership_date = membership_date;
        this.books = books;
    }

    public Member(long id, String name, LocalDateTime membership_date) {
        this.id = id;
        this.name = name;
        this.membership_date = membership_date;
    }

    public Member(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Member(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getMembership_date() {
        return membership_date;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @PrePersist
    protected void onCreate() {
        this.membership_date = LocalDateTime.now();
    }
}

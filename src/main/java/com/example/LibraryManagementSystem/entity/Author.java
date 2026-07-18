package com.example.LibraryManagementSystem.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "authors")
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First Name is required")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Column(nullable = false)
    private String lastName;


    public String getFirstName(){return firstName;}
    public void setFistName(String fistName){this.firstName = fistName;}

    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Set<Book> getBooks(){return books;}
    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}

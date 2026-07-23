package com.example.LibraryManagementSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.LibraryManagementSystem.entity.Author;
import com.example.LibraryManagementSystem.exception.AuthorAlreadyExistsException;
import com.example.LibraryManagementSystem.exception.CategoryAlreadyExistsException;
import com.example.LibraryManagementSystem.repository.AuthorRepository;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    public Author save(Author author){
        String normalizedFirstName = author.getFirstName()
                .trim()
                .toUpperCase();

        String normalizedLastName = author.getLastName()
                .trim()
                .toUpperCase();
        
                                            
        if (authorRepository.existsByFirstNameAndLastName(normalizedFirstName,
                                                          normalizedLastName)) 
            {
            throw new CategoryAlreadyExistsException(
                    "Author already exists"
            );
        }

        author.setFirstName(normalizedFirstName);
        author.setLastName(normalizedLastName);

        return authorRepository.save(author);
    }

    public Author update(Long id, Author author){
        Author existingAuthor = authorRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Author not Found!")
        );

        String normalizedFirstName = author.getFirstName()
                .trim() 
                .toUpperCase();

        String normalizedLastName = author.getLastName()
                .trim()
                .toUpperCase();

        boolean alreadyExists =
                authorRepository.existsByFirstNameAndLastNameAndIdNot(
                        normalizedFirstName,
                        normalizedLastName,
                        id
                );

        if(alreadyExists) {
            throw new AuthorAlreadyExistsException("Author already exists");
        }

        existingAuthor.setFirstName(normalizedFirstName);
        existingAuthor.setLastName(normalizedLastName);

        return authorRepository.save(existingAuthor);
    }

    public List<Author> findAll(){
        return authorRepository.findAll();
    }

    public Optional<Author> findById(Long id){
        return authorRepository.findById(id);
    }

    public void delete(Long id){
        authorRepository.deleteById(id);
    }

    //detail author search
    public Author findByAuthorFullName(String firstName, String lastName){
        return authorRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                    firstName.trim(),
                    lastName.trim()
                )
                .orElseThrow(()->
                    new RuntimeException("Author not found!")
            );
    }

    //loose author search by matching char
    public List<Author> findByAuthorNameLoose(String name){
        String value = name.trim();

        return authorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    value,
                    value
                );
    }
}

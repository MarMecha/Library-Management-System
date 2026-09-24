package com.example.LibraryManagementSystem.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.LibraryManagementSystem.dto.author.AuthorRequest;
import com.example.LibraryManagementSystem.entity.Author;
import com.example.LibraryManagementSystem.exception.AuthorNotFoundException;
import com.example.LibraryManagementSystem.exception.AuthorAlreadyExistsException;
import com.example.LibraryManagementSystem.repository.AuthorRepository;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    public Author save(AuthorRequest request){
        String normalizedFirstName = request.firstName()
                .trim()
                .toUpperCase(Locale.ROOT);

        String normalizedLastName = request.lastName()
                .trim()
                .toUpperCase(Locale.ROOT);
        
                                            
        if (authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
                normalizedFirstName,
                normalizedLastName)) 
            {
            throw new AuthorAlreadyExistsException(
                    "Author already exists"
            );
        }

        Author author = new Author();

        author.setFirstName(normalizedFirstName);
        author.setLastName(normalizedLastName);

        return authorRepository.save(author);
    }

    public Author update(Long id, AuthorRequest request){
        Author existingAuthor = authorRepository.findById(id)
            .orElseThrow(() ->
                new AuthorNotFoundException("Author not found!")
        );

        String normalizedFirstName = request.firstName()
                .trim()
                .toUpperCase(Locale.ROOT);

        String normalizedLastName = request.lastName()
                .trim()
                .toUpperCase(Locale.ROOT);

        boolean alreadyExists =
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndIdNot(
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

    public Page<Author> findAll(Pageable pageable){
        return authorRepository.findAll(pageable);
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
                    new AuthorNotFoundException("Author not found!")
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

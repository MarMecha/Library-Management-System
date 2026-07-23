package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagementSystem.entity.User;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}

/*
findAll()

findById()

save()

delete()

count()

existsById()
*/
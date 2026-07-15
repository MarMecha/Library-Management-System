package com.example.LibraryManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LibraryManagementSystem.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
}

/*
findAll()

findById()

save()

delete()

count()

existsById()
*/
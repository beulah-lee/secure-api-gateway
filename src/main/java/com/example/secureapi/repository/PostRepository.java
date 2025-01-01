package com.example.secureapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.secureapi.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {}
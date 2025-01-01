package com.example.secureapi.service;

import org.springframework.stereotype.Service;

import com.example.secureapi.model.Post;
import com.example.secureapi.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post getPost(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Post updatePost(Long id, Post updatedPost) {
        Post post = getPost(id);
        post.setContent(updatedPost.getContent());
        post.setTitle(updatedPost.getTitle());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
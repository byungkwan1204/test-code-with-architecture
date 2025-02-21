package com.example.demo.post.service;

import com.example.demo.global.exception.ResourceNotFoundException;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.PostEntity;
import com.example.demo.post.infrastructure.PostJpaRepository;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.UserService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public Post getPostById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    public Post createPost(PostCreate postCreate) {
        User user = userService.getById(postCreate.getWriterId());
        return postRepository.save(Post.create(postCreate, user));
    }

    public Post updatePost(long id, PostUpdate postUpdate) {
        Post post = getPostById(id);
        return postRepository.save(post.update(postUpdate));
    }
}
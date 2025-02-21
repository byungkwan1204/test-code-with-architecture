package com.example.demo.post.service.port;

import com.example.demo.post.domain.Post;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    Optional<Post> findById(Long id);
}

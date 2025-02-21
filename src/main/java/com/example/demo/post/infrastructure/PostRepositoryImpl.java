package com.example.demo.post.infrastructure;

import com.example.demo.post.domain.Post;
import com.example.demo.post.service.port.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        PostEntity postEntity = PostEntity.fromModel(post);
        return postJpaRepository.save(postEntity).toModel();
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id).map(PostEntity::toModel);
    }
}

package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.UserEntity;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post {

    private Long id;

    private String content;

    private Long createdAt;

    private Long modifiedAt;

    private User writer;

    public static Post create(PostCreate postCreate, User user) {
        return Post.builder()
            .content(postCreate.getContent())
            .writer(user)
            .createdAt(Clock.systemUTC().millis())
            .build();
    }

    public Post update(PostUpdate postUpdate) {
        return Post.builder()
            .id(this.id)
            .content(postUpdate.getContent())
            .createdAt(this.createdAt)
            .modifiedAt(Clock.systemUTC().millis())
            .writer(this.writer)
            .build();
    }
}

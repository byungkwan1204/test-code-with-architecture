package com.example.demo.post.infrastructure;

import com.example.demo.post.domain.Post;
import com.example.demo.user.infrastructure.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "modified_at")
    private Long modifiedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity writer;

    public Post toModel() {
        return Post.builder()
            .id(this.id)
            .content(this.content)
            .createdAt(this.createdAt)
            .modifiedAt(this.modifiedAt)
            .writer(writer.toModel())
            .build();
    }

    public static PostEntity fromModel(Post post) {
        return PostEntity.builder()
            .content(post.getContent())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .writer(UserEntity.fromModel(post.getWriter()))
            .build();
    }
}
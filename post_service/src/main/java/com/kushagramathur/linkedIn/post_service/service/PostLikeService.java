package com.kushagramathur.linkedIn.post_service.service;

import com.kushagramathur.linkedIn.post_service.entity.Post;
import com.kushagramathur.linkedIn.post_service.entity.PostLike;
import com.kushagramathur.linkedIn.post_service.exception.BadRequestException;
import com.kushagramathur.linkedIn.post_service.repository.PostLikeRepository;
import com.kushagramathur.linkedIn.post_service.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public void likePost(Long postId, Long userId) {
        log.info("User {} is liking post {}", userId, postId);

        postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        boolean hasAlreadyLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (hasAlreadyLiked) {
            throw new BadRequestException("You have already liked this post.");
        }

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        // TODO: Send notification to post owner about the new like
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        log.info("User {} is unliking post {}", userId, postId);

        postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        boolean hasAlreadyLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (!hasAlreadyLiked) {
            throw new BadRequestException("You cannot unlike a post you haven't liked.");
        }

        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}

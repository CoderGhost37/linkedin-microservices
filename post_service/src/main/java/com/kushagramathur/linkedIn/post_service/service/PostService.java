package com.kushagramathur.linkedIn.post_service.service;

import com.kushagramathur.linkedIn.post_service.auth.AuthContextHolder;
import com.kushagramathur.linkedIn.post_service.client.ConnectionsServiceClient;
import com.kushagramathur.linkedIn.post_service.dto.PersonDto;
import com.kushagramathur.linkedIn.post_service.dto.PostCreateRequestDto;
import com.kushagramathur.linkedIn.post_service.dto.PostDto;
import com.kushagramathur.linkedIn.post_service.entity.Post;
import com.kushagramathur.linkedIn.post_service.event.PostCreated;
import com.kushagramathur.linkedIn.post_service.exception.ResourceNotFoundException;
import com.kushagramathur.linkedIn.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;
    private final ConnectionsServiceClient connectionsServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto) {
        log.info("Creating post with content: {}", postCreateRequestDto.getContent());

        Long userId = AuthContextHolder.getCurrentUserId();

        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        post = postRepository.save(post);

        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId);
        for (PersonDto personDto : personDtoList) {
            PostCreated postCreatedEvent = PostCreated.builder()
                    .postId(post.getId())
                    .content(post.getContent())
                    .userId(personDto.getId())
                    .ownerUserId(userId)
                    .build();
            postCreatedKafkaTemplate.send("post_created_topic", postCreatedEvent);
        }

        return modelMapper.map(post, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.info("Fetching post with ID: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Fetching all posts for user ID: {}", userId);

        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

}

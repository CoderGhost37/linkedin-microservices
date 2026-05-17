package com.kushagramathur.linkedIn.user_service.service;

import com.kushagramathur.linkedIn.user_service.dto.LoginRequestDto;
import com.kushagramathur.linkedIn.user_service.dto.SignupRequestDto;
import com.kushagramathur.linkedIn.user_service.dto.UserDto;
import com.kushagramathur.linkedIn.user_service.entity.User;
import com.kushagramathur.linkedIn.user_service.event.UserCreatedEvent;
import com.kushagramathur.linkedIn.user_service.exception.BadRequestException;
import com.kushagramathur.linkedIn.user_service.exception.ResourceNotFoundException;
import com.kushagramathur.linkedIn.user_service.repository.UserRepository;
import com.kushagramathur.linkedIn.user_service.utils.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final JwtService jwtService;
    private final KafkaTemplate<String, UserCreatedEvent> userCreatedEventKafkaTemplate;

    public UserDto signup(SignupRequestDto signupRequestDto) {
        log.info("Signing up user with email: {}", signupRequestDto.getEmail());

        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if (exists) {
            log.warn("Email {} is already in use", signupRequestDto.getEmail());
            throw new BadRequestException("Email is already exists");
        }

        User user = mapper.map(signupRequestDto, User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));
        user = userRepository.save(user);

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();
        userCreatedEventKafkaTemplate.send("user_created_topic", userCreatedEvent);

        return mapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Logging in user with email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("User with email {} not found", loginRequestDto.getEmail());
                    return new ResourceNotFoundException("Invalid email or password");
                });

        boolean passwordMatch = BCrypt.match(loginRequestDto.getPassword(), user.getPassword());
        if (!passwordMatch) {
            log.warn("Invalid password for email {}", loginRequestDto.getEmail());
            throw new BadRequestException("Invalid email or password");
        }

        return jwtService.generateAccessToken(user);
    }
}

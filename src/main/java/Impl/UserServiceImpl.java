package Impl;

import exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repository.UserRepository;
import service.UserCacheService;
import service.UserService;
import dto.UserMapper;
import dto.UserRequestDto;
import dto.UserResponseDto;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCacheService userCacheService;

    @Override
    public Mono<UserResponseDto> createUser(UserRequestDto requestDto) {
        log.info("Creating new user with email: {}", requestDto.getEmail());
        User user = UserMapper.toEntity(requestDto);
        return userRepository.save(user)
                .map(UserMapper::toResponseDto)
                .doOnSuccess(response -> log.info("User created successfully with id: {}", response.getId()));
    }


    @Override
    public Mono<UserResponseDto> getUserById(Long id) {
        User cachedUser = userCacheService.getFromCache(id);

        if (cachedUser != null) {
            return Mono.just(UserMapper.toResponseDto(cachedUser));
        }

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .doOnNext(user -> userCacheService.putInCache(id, user))
                .map(UserMapper::toResponseDto);
    }
    @Override
    public Flux<UserResponseDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .map(UserMapper::toResponseDto);
    }

    @Override
    public Mono<UserResponseDto> updateUser(Long id, UserRequestDto  requestDto) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .flatMap(existingUser -> {
                    existingUser.setName(requestDto.getName());
                    existingUser.setEmail(requestDto.getEmail());
                    return userRepository.save(existingUser);
                })
                //.doOnSuccess(updatedUser -> userCacheService.putInCache(id, updatedUser));
                .doOnNext(updatedUser -> userCacheService.putInCache(id, updatedUser))
                .map(UserMapper::toResponseDto);
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .flatMap(userRepository::delete)
                .doOnSuccess(v -> userCacheService.evictFromCache(id));
    }

    @Override
    public Flux<User> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }
}
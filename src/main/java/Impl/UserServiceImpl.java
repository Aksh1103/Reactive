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
//
//    @Override
//    public Mono<User> createUser(User user) {
//        log.info("Creating new user with email: {}", user.getEmail());
//        return userRepository.save(user);
//    }

    @Override
    public Mono<UserResponseDto> createUser(UserRequestDto requestDto) {
        log.info("Creating new user with email: {}", requestDto.getEmail());
        User user = UserMapper.toEntity(requestDto);
        return userRepository.save(user)
                .map(UserMapper::toResponseDto)
                .doOnSuccess(response -> log.info("User created successfully with id: {}", response.getId()));
    }


    @Override
    public Mono<User> createUser(User user) {
        return null;
    }

    @Override
    public Mono<User> getUserById(Long id) {
        User cachedUser = userCacheService.getFromCache(id);

        if (cachedUser != null) {
            return Mono.just(cachedUser);
        }

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .doOnNext(user -> userCacheService.putInCache(id, user));
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> updateUser(Long id, User user) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                })
                .doOnSuccess(updatedUser -> userCacheService.putInCache(id, updatedUser));
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
package service;

import dto.UserRequestDto;
import dto.UserResponseDto;
import model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {


    Mono<UserResponseDto> getUserById(Long id);

    Flux<UserResponseDto> getAllUsers();

    Mono<UserResponseDto> updateUser(Long id, UserRequestDto  requestDto);

    Mono<Void> deleteUser(Long id);

    Flux<User> searchUsersByName(String name);

    Mono<UserResponseDto> createUser(UserRequestDto requestDto);
}

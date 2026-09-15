package service;

import model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> createUser(User user);

    Mono<User> getUserById(Long id);

    Flux<User> getAllUsers();

    Mono<User> updateUser(Long id, User user);

    Mono<Void> deleteUser(Long id);

    Flux<User> searchUsersByName(String name);
}

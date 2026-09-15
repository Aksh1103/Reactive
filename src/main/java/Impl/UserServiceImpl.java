package Impl;

import exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import repository.UserRepository;
import service.UserService;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl  implements UserService {

    private final UserRepository  userRepository;



    @Override
    public Mono<User> createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
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
                });
    }
    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
                .flatMap(userRepository::delete);
    }

//    @Override
//    public Flux<User> searchUsersByName(String name) {
//        return null;
//    }

    @Override
    public Flux<User> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }



}

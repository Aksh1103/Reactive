package controller;

import lombok.AllArgsConstructor;
import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import service.UserService;
import dto.UserRequestDto;
import dto.UserResponseDto;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private  final UserService  userService;


    @PostMapping
    public Mono<UserResponseDto> createUser(@RequestBody UserRequestDto requestDto) {
        log.info("Received request to create user: {}", requestDto.getEmail());
        return userService.createUser(requestDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponseDto>> updateUser(@PathVariable Long id, @RequestBody UserRequestDto  requestDto) {
        return userService.updateUser(id, requestDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
    @GetMapping("/search")
    public Flux<User> searchUsers(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }
}

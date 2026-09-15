package repository;

import model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

   // Mono<User> findByEmail(String email);

    Mono<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE name LIKE CONCAT('%', :name, '%')")
    Flux<User> findByNameContaining(String name);
}

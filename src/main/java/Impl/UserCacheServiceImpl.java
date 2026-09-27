package Impl;

import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.stereotype.Service;
import service.UserCacheService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserCacheServiceImpl implements UserCacheService {

    private final Map<Long, User> cache = new ConcurrentHashMap<>();

    @Override
    public User getFromCache(Long id) {
        User user = cache.get(id);
        if (user != null) {
            log.info("Cache HIT for user id: {}", id);
        } else {
            log.info("Cache MISS for user id: {}", id);
        }
        return user;
    }

    @Override
    public void putInCache(Long id, User user) {
        cache.put(id, user);
        log.info("User with id {} added to cache", id);
    }

    @Override
    public void evictFromCache(Long id) {
        cache.remove(id);
        log.info("User with id {} removed from cache", id);
    }
}
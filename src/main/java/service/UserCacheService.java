package service;

import model.User;

public interface UserCacheService {
    User getFromCache(Long id);
    void putInCache(Long id, User user);
    void evictFromCache(Long id);
}

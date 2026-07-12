package com.firstclub.membership.repository;

import com.firstclub.membership.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public User create(String name, String email, String cohort) {
        long id = idGenerator.getAndIncrement();
        User user = new User(id, name, email, cohort);
        users.put(id, user);
        return user;
    }

    public User findById(long id) {
        return users.get(id);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}

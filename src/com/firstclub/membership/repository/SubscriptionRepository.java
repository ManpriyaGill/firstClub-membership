package com.firstclub.membership.repository;

import com.firstclub.membership.model.Subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class SubscriptionRepository {

    private final Map<Long, Subscription> byUserId = new ConcurrentHashMap<>();
    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public long nextId() {
        return idGenerator.getAndIncrement();
    }

    public ReentrantLock lockFor(long userId) {
        return userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
    }

    public Subscription findByUserId(long userId) {
        return byUserId.get(userId);
    }

    public void save(Subscription subscription) {
        byUserId.put(subscription.getUserId(), subscription);
    }
}

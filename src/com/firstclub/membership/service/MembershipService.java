package com.firstclub.membership.service;

import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.factory.MembershipPlanFactory;
import com.firstclub.membership.factory.TierFactory;
import com.firstclub.membership.model.*;
import com.firstclub.membership.repository.OrderStatsRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.UserRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class MembershipService {

    private final MembershipPlanFactory planFactory;
    private final TierFactory tierFactory;
    private final TierEvaluationService tierEvaluationService;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final OrderStatsRepository orderStatsRepository;

    public MembershipService(MembershipPlanFactory planFactory,
                             TierFactory tierFactory,
                             TierEvaluationService tierEvaluationService,
                             SubscriptionRepository subscriptionRepository,
                             UserRepository userRepository,
                             OrderStatsRepository orderStatsRepository) {
        this.planFactory = planFactory;
        this.tierFactory = tierFactory;
        this.tierEvaluationService = tierEvaluationService;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.orderStatsRepository = orderStatsRepository;
    }

    public Map<PlanType, MembershipPlan> getAvailablePlans() {
        return planFactory.getAllPlans();
    }

    public Map<TierLevel, Tier> getAvailableTiers() {
        return tierFactory.getAllTiers();
    }

    /**
     * Subscribe a user to a plan. Tier defaults to SILVER (base) unless the user already qualifies higher.
     */
    public Subscription subscribe(long userId, PlanType planType, boolean autoRenew) {
        User user = requireUser(userId);
        ReentrantLock lock = subscriptionRepository.lockFor(userId);
        lock.lock();
        try {
            Subscription existing = subscriptionRepository.findByUserId(userId);
            if (existing != null && existing.isCurrentlyActive()) {
                throw new MembershipException("User already has an active subscription. Use upgrade/downgrade/changePlan instead.");
            }
            MembershipPlan plan = planFactory.createPlan(planType);
            OrderStats stats = orderStatsRepository.getOrCreate(userId);
            TierLevel qualifiedTier = tierEvaluationService.evaluate(user, stats);
            Tier startingTier = tierFactory.getTier(qualifiedTier);

            LocalDate start = LocalDate.now();
            LocalDate expiry = start.plusDays(plan.getDurationInDays());
            Subscription subscription = new Subscription(
                    subscriptionRepository.nextId(), userId, plan, startingTier, start, expiry, autoRenew);
            subscriptionRepository.save(subscription);
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    public Subscription changePlan(long userId, PlanType newPlanType) {
        ReentrantLock lock = subscriptionRepository.lockFor(userId);
        lock.lock();
        try {
            Subscription subscription = requireActiveSubscription(userId);
            MembershipPlan newPlan = planFactory.createPlan(newPlanType);
            subscription.setPlan(newPlan);
            // extend expiry from today for the new plan's duration
            subscription.setExpiryDate(LocalDate.now().plusDays(newPlan.getDurationInDays()));
            subscriptionRepository.save(subscription);
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    public Subscription upgradeTier(long userId, TierLevel targetTier) {
        ReentrantLock lock = subscriptionRepository.lockFor(userId);
        lock.lock();
        try {
            Subscription subscription = requireActiveSubscription(userId);
            if (!targetTier.isHigherThan(subscription.getTier().getLevel())) {
                throw new MembershipException("Target tier must be higher than current tier for an upgrade.");
            }
            subscription.setTier(tierFactory.getTier(targetTier));
            subscriptionRepository.save(subscription);
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    public Subscription downgradeTier(long userId, TierLevel targetTier) {
        ReentrantLock lock = subscriptionRepository.lockFor(userId);
        lock.lock();
        try {
            Subscription subscription = requireActiveSubscription(userId);
            if (!targetTier.isLowerThan(subscription.getTier().getLevel())) {
                throw new MembershipException("Target tier must be lower than current tier for a downgrade.");
            }
            subscription.setTier(tierFactory.getTier(targetTier));
            subscriptionRepository.save(subscription);
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    public Subscription cancelSubscription(long userId) {
        ReentrantLock lock = subscriptionRepository.lockFor(userId);
        lock.lock();
        try {
            Subscription subscription = requireActiveSubscription(userId);
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setAutoRenew(false);
            subscriptionRepository.save(subscription);
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    public Subscription getCurrentMembership(long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId);
        if (subscription == null) {
            throw new MembershipException("No subscription found for user " + userId);
        }
        // lazily mark expired subscriptions when read
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE && !subscription.isCurrentlyActive()) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
        }
        return subscription;
    }

    public Subscription recordOrder(long userId, double orderValue) {
        User user = requireUser(userId);
        OrderStats stats = orderStatsRepository.getOrCreate(userId);
        stats.recordOrder(orderValue);

        ReentrantLock lock = subscriptionRepository.lockFor(userId);
        lock.lock();
        try {
            Subscription subscription = subscriptionRepository.findByUserId(userId);
            if (subscription == null || !subscription.isCurrentlyActive()) {
                return subscription; // no active membership to promote
            }
            TierLevel qualifiedTier = tierEvaluationService.evaluate(user, stats);
            if (qualifiedTier.isHigherThan(subscription.getTier().getLevel())) {
                subscription.setTier(tierFactory.getTier(qualifiedTier));
                subscriptionRepository.save(subscription);
            }
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    private Subscription requireActiveSubscription(long userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId);
        if (subscription == null || !subscription.isCurrentlyActive()) {
            throw new MembershipException("User " + userId + " has no active subscription.");
        }
        return subscription;
    }

    private User requireUser(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new MembershipException("User not found: " + userId);
        }
        return user;
    }
}

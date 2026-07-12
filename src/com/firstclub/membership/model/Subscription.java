package com.firstclub.membership.model;

import com.firstclub.membership.enums.SubscriptionStatus;

import java.time.LocalDate;

public final class Subscription {
    private final long id;
    private final long userId;
    private MembershipPlan plan;
    private Tier tier;
    private final LocalDate startDate;
    private LocalDate expiryDate;
    private SubscriptionStatus status;
    private boolean autoRenew;

    public Subscription(long id, long userId, MembershipPlan plan, Tier tier,
                         LocalDate startDate, LocalDate expiryDate, boolean autoRenew) {
        this.id = id;
        this.userId = userId;
        this.plan = plan;
        this.tier = tier;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.autoRenew = autoRenew;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public void setPlan(MembershipPlan plan) {
        this.plan = plan;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public boolean isCurrentlyActive() {
        return status == SubscriptionStatus.ACTIVE && !expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Subscription{id=" + id + ", userId=" + userId + ", plan=" + plan.getType()
                + ", tier=" + tier.getLevel() + ", status=" + status
                + ", start=" + startDate + ", expiry=" + expiryDate + ", autoRenew=" + autoRenew + "}";
    }
}

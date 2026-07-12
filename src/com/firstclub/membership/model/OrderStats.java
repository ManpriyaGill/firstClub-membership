package com.firstclub.membership.model;

import java.time.YearMonth;

public final class OrderStats {
    private final long userId;
    private int orderCountThisMonth;
    private double totalOrderValueThisMonth;
    private YearMonth currentMonth;

    public OrderStats(long userId) {
        this.userId = userId;
        this.currentMonth = YearMonth.now();
    }

    public synchronized void recordOrder(double orderValue) {
        rolloverIfNewMonth();
        this.orderCountThisMonth++;
        this.totalOrderValueThisMonth += orderValue;
    }

    private void rolloverIfNewMonth() {
        YearMonth now = YearMonth.now();
        if (!now.equals(currentMonth)) {
            currentMonth = now;
            orderCountThisMonth = 0;
            totalOrderValueThisMonth = 0;
        }
    }

    public synchronized int getOrderCountThisMonth() {
        rolloverIfNewMonth();
        return orderCountThisMonth;
    }

    public synchronized double getTotalOrderValueThisMonth() {
        rolloverIfNewMonth();
        return totalOrderValueThisMonth;
    }

    public long getUserId() {
        return userId;
    }
}

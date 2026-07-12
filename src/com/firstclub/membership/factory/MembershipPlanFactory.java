package com.firstclub.membership.factory;

import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.model.MembershipPlan;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MembershipPlanFactory {

    private final Map<PlanType, BigDecimal> pricing = new ConcurrentHashMap<>();

    public MembershipPlanFactory() {
        // default pricing
        pricing.put(PlanType.MONTHLY, new BigDecimal("199"));
        pricing.put(PlanType.QUARTERLY, new BigDecimal("499"));
        pricing.put(PlanType.YEARLY, new BigDecimal("1499"));
    }

    public MembershipPlan createPlan(PlanType type) {
        BigDecimal price = pricing.get(type);
        if (price == null) {
            throw new MembershipException("No pricing configured for plan: " + type);
        }
        int durationInDays = type.getDurationInMonths() * 30;
        return new MembershipPlan(type, price, durationInDays);
    }

    public Map<PlanType, MembershipPlan> getAllPlans() {
        Map<PlanType, MembershipPlan> all = new EnumMap<>(PlanType.class);
        for (PlanType type : PlanType.values()) {
            all.put(type, createPlan(type));
        }
        return all;
    }

    public void updatePrice(PlanType type, BigDecimal newPrice) {
        pricing.put(type, newPrice);
    }
}

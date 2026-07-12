package com.firstclub.membership.factory;

import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.model.Benefit;
import com.firstclub.membership.model.Tier;
import com.firstclub.membership.model.TierCriteria;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class TierFactory {

    private final Map<TierLevel, Tier> registry = new ConcurrentHashMap<>();

    public TierFactory() {
        registerDefaultTiers();
    }

    private void registerDefaultTiers() {
        registerTier(new Tier(
                TierLevel.SILVER,
                Arrays.asList(
                        new Benefit(BenefitType.FREE_DELIVERY, "Free delivery above minimum order", 0)
                ),
                new TierCriteria(0, 0, Collections.emptySet()) // everyone qualifies by default
        ));

        registerTier(new Tier(
                TierLevel.GOLD,
                Arrays.asList(
                        new Benefit(BenefitType.FREE_DELIVERY, "Free delivery, no minimum", 0),
                        new Benefit(BenefitType.PERCENTAGE_DISCOUNT, "Extra discount on selected items", 10),
                        new Benefit(BenefitType.EARLY_ACCESS, "Early access to sales", 0)
                ),
                new TierCriteria(5, 5000, Set.of("EMPLOYEE"))
        ));

        registerTier(new Tier(
                TierLevel.PLATINUM,
                Arrays.asList(
                        new Benefit(BenefitType.FREE_DELIVERY, "Free express delivery", 0),
                        new Benefit(BenefitType.PERCENTAGE_DISCOUNT, "Extra discount on selected items", 20),
                        new Benefit(BenefitType.EARLY_ACCESS, "Earliest access to sales", 0),
                        new Benefit(BenefitType.PRIORITY_SUPPORT, "Priority customer support", 0),
                        new Benefit(BenefitType.EXCLUSIVE_COUPON, "Exclusive monthly coupons", 0)
                ),
                new TierCriteria(15, 20000, Set.of("INFLUENCER"))
        ));
    }

    public void registerTier(Tier tier) {
        registry.put(tier.getLevel(), tier);
    }

    public Tier getTier(TierLevel level) {
        Tier tier = registry.get(level);
        if (tier == null) {
            throw new MembershipException("No tier configured for level: " + level);
        }
        return tier;
    }

    public Map<TierLevel, Tier> getAllTiers() {
        Map<TierLevel, Tier> ordered = new TreeMap<>(Comparator.comparingInt(TierLevel::ordinal));
        ordered.putAll(registry);
        return ordered;
    }
}

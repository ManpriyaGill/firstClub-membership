package com.firstclub.membership;

import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.factory.MembershipPlanFactory;
import com.firstclub.membership.factory.TierFactory;
import com.firstclub.membership.model.Subscription;
import com.firstclub.membership.model.User;
import com.firstclub.membership.repository.OrderStatsRepository;
import com.firstclub.membership.repository.SubscriptionRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.MembershipService;
import com.firstclub.membership.service.TierEvaluationService;
import com.firstclub.membership.strategy.CohortTierStrategy;
import com.firstclub.membership.strategy.CompositeTierStrategy;
import com.firstclub.membership.strategy.OrderCountTierStrategy;
import com.firstclub.membership.strategy.OrderValueTierStrategy;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        MembershipPlanFactory planFactory = new MembershipPlanFactory();
        TierFactory tierFactory = new TierFactory();

        TierEvaluationService tierEvaluationService = new TierEvaluationService(
                tierFactory,
                new CompositeTierStrategy(List.of(
                        new OrderCountTierStrategy(),
                        new OrderValueTierStrategy(),
                        new CohortTierStrategy()
                ))
        );

        UserRepository userRepository = new UserRepository();
        OrderStatsRepository orderStatsRepository = new OrderStatsRepository();
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository();

        MembershipService membershipService = new MembershipService(
                planFactory, tierFactory, tierEvaluationService,
                subscriptionRepository, userRepository, orderStatsRepository);

        System.out.println("FirstClub Membership - Demo\n");

        User alice = userRepository.create("Alice", "alice@example.com", "GENERAL");
        User bob = userRepository.create("Bob", "bob@example.com", "EMPLOYEE");

        System.out.println("1) Available plans: " + membershipService.getAvailablePlans().values());
        System.out.println("2) Available tiers: " + membershipService.getAvailableTiers().keySet());

        Subscription aliceSub = membershipService.subscribe(alice.getId(), PlanType.MONTHLY, true);
        System.out.println("\n3) Alice subscribed -> " + aliceSub);

        Subscription bobSub = membershipService.subscribe(bob.getId(), PlanType.YEARLY, true);
        System.out.println("4) Bob subscribed (EMPLOYEE cohort, auto-qualifies GOLD) -> " + bobSub);

        // Alice places enough orders to auto cross into GOLD
        for (int i = 0; i < 6; i++) {
            membershipService.recordOrder(alice.getId(), 800);
        }
        System.out.println("\n5) After 6 orders, Alice auto-promoted -> " + membershipService.getCurrentMembership(alice.getId()));

        Subscription upgraded = membershipService.upgradeTier(alice.getId(), TierLevel.PLATINUM);
        System.out.println("6) Alice manually upgraded to PLATINUM -> " + upgraded);

        Subscription changedPlan = membershipService.changePlan(alice.getId(), PlanType.YEARLY);
        System.out.println("7) Alice changed plan to YEARLY -> " + changedPlan);

        Subscription cancelled = membershipService.cancelSubscription(bob.getId());
        System.out.println("8) Bob cancelled -> " + cancelled);

        //concurrency demo
        System.out.println("\n9) Concurrency demo: 20 threads recording orders for Carol simultaneously...");
        User carol = userRepository.create("Carol", "carol@example.com", "GENERAL");
        membershipService.subscribe(carol.getId(), PlanType.MONTHLY, true);

        int threadCount = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    membershipService.recordOrder(carol.getId(), 1000);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        pool.shutdown();
        System.out.println("   All 20 concurrent orders recorded safely -> " + membershipService.getCurrentMembership(carol.getId()));
        System.out.println("   (No lost updates thanks to per-user locking + synchronized OrderStats)");
    }
}

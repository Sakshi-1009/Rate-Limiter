package ratelimiter;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int maxAllowed = 5;
        long duration = 2000;

        System.out.println("--- Fixed Window Rate Limiter Test ---");
        RateLimiter fixedLimiter = new FixedWindowRateLimiter(maxAllowed, duration);
        ExternalService svc = new ExternalService(fixedLimiter);

        for (int idx = 0; idx < 7; idx++) {
            svc.processRequest("user123");
        }

        Thread.sleep(2000);
        System.out.println("\nAfter window expiration:");
        svc.processRequest("user123");

        System.out.println("\n--- Switching to Sliding Window Rate Limiter ---");
        svc.setRateLimiter(new SlidingWindowRateLimiter(maxAllowed, duration));

        for (int idx = 0; idx < 7; idx++) {
            svc.processRequest("user456");
        }
    }
}

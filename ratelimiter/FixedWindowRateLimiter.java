package ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowRateLimiter implements RateLimiter {
    private final int threshold;
    private final long intervalMillis;
    private final ConcurrentHashMap<String, BucketData> buckets = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int threshold, long intervalMillis) {
        this.threshold = threshold;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public boolean isAllowed(String clientKey) {
        long currentTime = System.currentTimeMillis();
        long bucketId = currentTime / intervalMillis;

        BucketData bucket = buckets.compute(clientKey, (k, existing) -> {
            if (existing == null || existing.bucketId != bucketId) {
                return new BucketData(bucketId, 0);
            }
            return existing;
        });

        if (bucket.requestCount.get() < threshold) {
            if (bucket.requestCount.getAndIncrement() < threshold) {
                return true;
            }
        }
        return false;
    }

    private static class BucketData {
        final long bucketId;
        final AtomicLong requestCount;

        BucketData(long bucketId, long startCount) {
            this.bucketId = bucketId;
            this.requestCount = new AtomicLong(startCount);
        }
    }
}

package ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SlidingWindowRateLimiter implements RateLimiter {
    private final int maxAllowed;
    private final long frameDuration;
    private final ConcurrentHashMap<String, FrameData> tracker = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxAllowed, long frameDuration) {
        this.maxAllowed = maxAllowed;
        this.frameDuration = frameDuration;
    }

    @Override
    public boolean isAllowed(String clientKey) {
        long currentTime = System.currentTimeMillis();
        long frameId = currentTime / frameDuration;

        FrameData frame = tracker.compute(clientKey, (k, existing) -> {
            if (existing == null)
                return new FrameData(frameId);
            if (existing.activeFrameId != frameId) {
                existing.previousCount = (existing.activeFrameId == frameId - 1) ? existing.activeCount.get() : 0;
                existing.activeCount.set(0);
                existing.activeFrameId = frameId;
            }
            return existing;
        });

        double overlap = (double) (frameDuration - (currentTime % frameDuration)) / frameDuration;

        while (true) {
            long currentActive = frame.activeCount.get();
            double approxCount = currentActive + (frame.previousCount * overlap);

            if (approxCount >= maxAllowed) {
                return false;
            }

            if (frame.activeCount.compareAndSet(currentActive, currentActive + 1)) {
                return true;
            }
        }
    }

    private static class FrameData {
        long activeFrameId;
        AtomicLong activeCount = new AtomicLong(0);
        long previousCount = 0;

        FrameData(long frameId) {
            this.activeFrameId = frameId;
        }
    }
}

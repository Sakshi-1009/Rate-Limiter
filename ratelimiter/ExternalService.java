package ratelimiter;

public class ExternalService {
    private RateLimiter limiter;

    public ExternalService(RateLimiter limiter) {
        this.limiter = limiter;
    }

    public void setRateLimiter(RateLimiter limiter) {
        this.limiter = limiter;
    }

    public void processRequest(String clientId) {
        if (limiter.isAllowed(clientId)) {
            invokeApi(clientId);
        } else {
            System.out.println("Rate limit exceeded for client: " + clientId);
        }
    }

    private void invokeApi(String clientId) {
        System.out.println("Successfully called external API for client: " + clientId);
    }
}

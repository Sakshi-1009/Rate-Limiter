# Rate Limiter

A Java-based rate limiting library implementing two popular algorithms: **Fixed Window** and **Sliding Window**.

## Algorithms

### 1. Fixed Window Rate Limiter
Divides time into fixed discrete intervals (or windows). Each window has a counter that starts at 0. If the counter exceeds the allowed threshold within that time window, subsequent requests are dropped until the next window starts.

### 2. Sliding Window Rate Limiter
A smoother approach that averages the traffic from the previous time frame and the current time frame, weighted by the overlap. This mitigates the "burst at the window boundary" problem common with the Fixed Window algorithm. 

*Note: The implementation is thread-safe and lock-free using `ConcurrentHashMap` and Atomic variables loop CAS.*

## Usage

You can compile and run the provided tests in `Main.java` using any standard Java compiler.

### Compile
```bash
javac ratelimiter/*.java
```

### Run
```bash
java ratelimiter.Main
```

## Known Issues / Future Improvements
- **Memory Management**: The `ConcurrentHashMap` stores a token bucket / frame data for every `clientKey` indefinitely. If the number of unique clients is unbounded, this will lead to a memory leak over time. A mechanism to evict inactive client keys (such as using Guava Cache or a background cleanup thread) is needed for a true production environment.

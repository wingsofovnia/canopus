package eu.ioservices.canopus.circuitbreaking;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class CircuitBreaker {
    private enum State {
        OPEN, CLOSED, HALF_OPEN
    }

    public static final long TIMEOUT_DEFAULT = 15 * 1000L;
    private long timeout = TIMEOUT_DEFAULT;
    private State state = State.CLOSED;
    private long lastStateChange = System.currentTimeMillis();
    public CircuitBreaker() {
    }

    public CircuitBreaker(long timeout) {
        this.timeout = timeout;
    }

    public boolean isClosed() {
        this.validateState();
        return this.state == State.CLOSED;
    }

    public boolean isOpen() {
        this.validateState();
        return this.state == State.OPEN;
    }

    public boolean isHalfOpen() {
        this.validateState();
        return this.state == State.HALF_OPEN;
    }

    public void trip() {
        this.state = State.OPEN;
        this.lastStateChange = System.currentTimeMillis();
    }

    public void release() {
        this.state = State.CLOSED;
        this.lastStateChange = System.currentTimeMillis();
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public <R> R process(Callable<R> command, Callable<R> fallback) throws CircuitBreakerException, CommandExecutionException {
        try {
            if (this.isClosed()) {
                return this.execute(Objects.requireNonNull(command));
            } else if (this.isHalfOpen()) {
                R result = this.execute(Objects.requireNonNull(command));
                this.release();
                return result;
            } else { // this.isOpen ~ true
                if (fallback != null) {
                    try {
                        return fallback.call();
                    } catch (Exception fe) {
                        throw new CircuitBreakerException("Failed to execute fallback", fe);
                    }
                } else {
                    throw new CircuitBreakerException("Circuit Breaker is Opened (no fallback given)");
                }
            }

        } catch (CircuitBreakerException e) {
            this.trip();
            if (fallback != null) {
                try {
                    return fallback.call();
                } catch (Exception fe) {
                    throw new CircuitBreakerException("Failed to execute fallback", fe);
                }
            } else {
                throw new CircuitBreakerException("Executing failed (no fallback given)", e);
            }
        }
    }

    public <R> R process(Callable<R> command) throws CircuitBreakerException, CommandExecutionException {
        return this.process(command, null);
    }

    protected <R> R execute(Callable<R> command) throws CircuitBreakerException, CommandExecutionException {
        Future<R> futureResult = Executors.newSingleThreadExecutor().submit(command);
        try {
            return futureResult.get(this.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new CircuitBreakerException("Command execution timeout", e);
        } catch (InterruptedException e) {
            throw new CircuitBreakerException("Failed to execute command", e);
        } catch (ExecutionException e) {
            throw new CommandExecutionException("Failed to execute command", e);
        }
    }

    protected void validateState() {
        if (this.state != State.CLOSED) {
            if (System.currentTimeMillis() - this.lastStateChange >= this.getTimeout()) {
                if (this.state == State.OPEN) {
                    this.state = State.HALF_OPEN;
                } else {
                    this.state = State.CLOSED;
                }
            }
        }
    }
}

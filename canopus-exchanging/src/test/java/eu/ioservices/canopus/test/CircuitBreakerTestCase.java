package eu.ioservices.canopus.test;

import eu.ioservices.canopus.circuitbreaking.CircuitBreaker;
import eu.ioservices.canopus.circuitbreaking.CircuitBreakerException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class CircuitBreakerTestCase {
    @Test(expected = CircuitBreakerException.class)
    public void timeoutTest() {
        CircuitBreaker circuitBreaker = new CircuitBreaker();
        circuitBreaker.setTimeout(100L);

        circuitBreaker.process(() -> {
            while (true) {
                // do something
            }
        });
    }

    @Test(expected = CircuitBreakerException.class)
    public void closedCircuitBreakerTest() {
        CircuitBreaker circuitBreaker = new CircuitBreaker();
        circuitBreaker.setTimeout(100L);

        try {
            circuitBreaker.process(() -> {
                while (true) {
                    // do something
                }
            });
        } catch (CircuitBreakerException e) {
            assertTrue(circuitBreaker.isOpen());
            circuitBreaker.process(() -> {
                System.out.println("Print command in opened circuit breaker");
                return null;
            });
        }
    }


    @Test
    public void halfOpenTestCircuitBreakerTest() throws InterruptedException {
        CircuitBreaker circuitBreaker = new CircuitBreaker();
        circuitBreaker.setTimeout(100L);

        try {
            circuitBreaker.process(() -> {
                while (true) {
                    // do something
                }
            });
        } catch (CircuitBreakerException e) {
            assertTrue("CircuitBreaker must be opened", circuitBreaker.isOpen());
            Thread.sleep(100L);
            assertTrue("CircuitBreaker must be HALF-opened", circuitBreaker.isHalfOpen());
            circuitBreaker.process(() -> {
                // some command to half-opened circuit breaker
                return null;
            });
        }
    }

    @Test
    public void reClosedCircuitBreakerTest() throws InterruptedException {
        CircuitBreaker circuitBreaker = new CircuitBreaker();
        circuitBreaker.setTimeout(100L);

        try {
            circuitBreaker.process(() -> {
                while (true) {
                    // do something
                }
            });
        } catch (CircuitBreakerException e) {
            assertTrue("CircuitBreaker must be opened", circuitBreaker.isOpen());
            Thread.sleep(100L);
            assertTrue("CircuitBreaker must be HALF-opened", circuitBreaker.isHalfOpen());
            Thread.sleep(100L);
            assertTrue("CircuitBreaker must be closed", circuitBreaker.isClosed());
        }
    }
}

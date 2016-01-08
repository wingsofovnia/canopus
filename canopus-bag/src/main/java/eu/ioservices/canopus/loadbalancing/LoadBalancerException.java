package eu.ioservices.canopus.loadbalancing;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class LoadBalancerException extends RuntimeException {
    public LoadBalancerException() {
        super();
    }

    public LoadBalancerException(String message) {
        super(message);
    }

    public LoadBalancerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadBalancerException(Throwable cause) {
        super(cause);
    }

    protected LoadBalancerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

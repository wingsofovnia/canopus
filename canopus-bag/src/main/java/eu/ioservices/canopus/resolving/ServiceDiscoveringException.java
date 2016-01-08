package eu.ioservices.canopus.resolving;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class ServiceDiscoveringException extends RuntimeException {
    public ServiceDiscoveringException() {
        super();
    }

    public ServiceDiscoveringException(String message) {
        super(message);
    }

    public ServiceDiscoveringException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceDiscoveringException(Throwable cause) {
        super(cause);
    }

    protected ServiceDiscoveringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package eu.ioservices.canopus.resolving;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class ServiceRegistrationException extends Exception {
    public ServiceRegistrationException() {
        super();
    }

    public ServiceRegistrationException(String message) {
        super(message);
    }

    public ServiceRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceRegistrationException(Throwable cause) {
        super(cause);
    }

    protected ServiceRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

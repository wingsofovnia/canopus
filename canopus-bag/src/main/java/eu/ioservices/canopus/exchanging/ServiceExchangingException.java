package eu.ioservices.canopus.exchanging;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class ServiceExchangingException extends RuntimeException {
    public ServiceExchangingException() {
    }

    public ServiceExchangingException(String message) {
        super(message);
    }

    public ServiceExchangingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceExchangingException(Throwable cause) {
        super(cause);
    }

    public ServiceExchangingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

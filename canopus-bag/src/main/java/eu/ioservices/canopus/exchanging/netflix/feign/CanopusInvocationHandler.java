package eu.ioservices.canopus.exchanging.netflix.feign;

import eu.ioservices.canopus.circuitbreaking.CircuitBreaker;
import eu.ioservices.canopus.circuitbreaking.CircuitBreakerException;
import eu.ioservices.canopus.exchanging.ServiceExchangingException;
import eu.ioservices.canopus.exchanging.annotations.Fallback;
import eu.ioservices.canopus.exchanging.annotations.Repeat;
import eu.ioservices.canopus.exchanging.annotations.Timeout;
import feign.InvocationHandlerFactory.MethodHandler;
import feign.Target;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class CanopusInvocationHandler implements InvocationHandler {
    private static final int DEFAULT_REPEAT_TIMES = 0;
    private Map<Method, MethodHandler> dispatch;

    public CanopusInvocationHandler(Target<?> target, Map<Method, MethodHandler> dispatch) {
        this.dispatch = dispatch;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Fallback fallbackAnnotation = method.getAnnotation(Fallback.class);
        final Repeat repeatAnnotation = method.getAnnotation(Repeat.class);

        if (fallbackAnnotation == null && repeatAnnotation == null)
            return this.dispatch.get(method).invoke(args);

        final Timeout timeoutAnnotation = method.getAnnotation(Timeout.class);
        if (timeoutAnnotation == null)
            throw new ServiceExchangingException("You must annotate method with @Timeout " +
                    "if you use @Fallback or @Repeat annotation");

        final Class<?> fallbackHandlerClass = fallbackAnnotation != null ? fallbackAnnotation.value() : null;
        final int repeatTimes = repeatAnnotation == null ? DEFAULT_REPEAT_TIMES : repeatAnnotation.value();
        final long timeout = timeoutAnnotation.value();

        final CircuitBreaker circuitBreaker = new CircuitBreaker(timeout);

        // Trying to execute method and repeat, if necessary
        for (int i = 0; i <= repeatTimes; i++) {
            try {
                return circuitBreaker.process(() -> {
                    try {
                        return CanopusInvocationHandler.this.dispatch.get(method).invoke(args);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                });
            } catch (CircuitBreakerException e) {
                circuitBreaker.release(); // and try again
            }
        }

        if (fallbackHandlerClass == null)
            throw new ServiceExchangingException("Failed to execute method #" + method.getName()
                    + " with timeout = " + timeout + "ms. Repeat times = " + repeatTimes
                    + ", fallback handler is not defined.");

        // Delegating execution to fallbackHandlerClass
        final String failedMethodName = method.getName();
        final Class<?>[] failedMethodParameterTypes = method.getParameterTypes();

        final Object fallbackHandlerObject = fallbackHandlerClass.newInstance();
        final Method fallbackHandlerMethod
                = proxy.getClass().getDeclaredMethod(failedMethodName, failedMethodParameterTypes);

        if (fallbackHandlerMethod == null)
            throw new ServiceExchangingException("Failed to find fallback method #" + method.getName()
                    + " at " + fallbackHandlerClass.getCanonicalName());

        return fallbackHandlerMethod.invoke(fallbackHandlerObject, args);
    }
}
package eu.ioservices.canopus.gateway.routing;

import eu.ioservices.canopus.http.HttpMethod;

import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Route {
    private final HttpMethod method;
    private final Pattern from;
    private final Pattern to;

    public Route(HttpMethod method, Pattern from, Pattern to) {
        this.method = Objects.requireNonNull(method);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
    }

    public static Route of(HttpMethod method, Pattern from, Pattern to) {
        return new Route(method, from, to);
    }

    public HttpMethod method() {
        return method;
    }

    public Pattern from() {
        return from;
    }

    public Pattern to() {
        return to;
    }
}

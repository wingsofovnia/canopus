package eu.ioservices.canopus.gateway.routing;

import eu.ioservices.canopus.http.HttpMethod;

import java.util.*;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Router {
    private List<Route> routes = new LinkedList<>();

    public Router addRoute(Route route) {
        this.routes.add(Objects.requireNonNull(route));
        return this;
    }

    public Router addRoute(HttpMethod method, String from, String to) {
        return addRoute(new Route(method, Pattern.of(from), Pattern.of(to)));
    }

    public Router addRoute(HttpMethod method, Pattern from, Pattern to) {
        return addRoute(new Route(method, from, to));
    }

    public Optional<Track> route(HttpMethod method, String target) {
        final Optional<Route> routeOpt = this.routes
                .stream()
                .filter(r -> r.method() == method
                             && r.from().matches(target))
                .findFirst();

        if (!routeOpt.isPresent())
            return Optional.empty();

        final Route route = routeOpt.get();
        return Optional.of(new Track(route, target));
    }

    public boolean removeRoute(String from) {
        return removeRoute(Pattern.of(from));
    }

    public boolean removeRoute(Pattern from) {
        final Iterator<Route> iterator = this.routes.iterator();

        while (iterator.hasNext()) {
            final Route next = iterator.next();
            if (next.from().equals(from)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }
}

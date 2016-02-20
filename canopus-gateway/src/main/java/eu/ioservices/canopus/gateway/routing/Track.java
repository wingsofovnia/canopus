package eu.ioservices.canopus.gateway.routing;

import java.util.Map;
import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Track {
    private final Route route;
    private final Map<String, String> parameters;
    private final String from;
    private final String to;

    public Track(Route route, String sourceUrl) {
        this.route = Objects.requireNonNull(route);

        final Pattern fromPattern = route.from();
        this.parameters = fromPattern.match(sourceUrl);

        final Pattern toPattern = route.to();
        final String compiledPattern = toPattern.compile(this.parameters);

        this.from = sourceUrl;
        this.to = compiledPattern;
    }

    public Route route() {
        return route;
    }

    public Map<String, String> params() {
        return parameters;
    }

    public String from() {
        return from;
    }

    public String to() {
        return to;
    }
}

package eu.ioservices.canopus.gateway.handling;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.discovery.ServiceDiscoverer;
import eu.ioservices.canopus.gateway.routing.Track;
import eu.ioservices.canopus.loadbalancing.LoadBalancer;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class DynamicRequestProcessor extends ForwardingRequestProcessor {
    private final ServiceDiscoverer serviceDiscoverer;
    private final LoadBalancer loadBalancer;

    public DynamicRequestProcessor(ServiceDiscoverer serviceDiscoverer, LoadBalancer loadBalancer) {
        this.serviceDiscoverer = Objects.requireNonNull(serviceDiscoverer);
        this.loadBalancer = Objects.requireNonNull(loadBalancer);
    }

    @Override
    public void process(Request req, Response res, Optional<Track> trackOpt) throws Exception {
        if (!trackOpt.isPresent()) {
            res.status(HttpStatus.BAD_GATEWAY_502);
            return;
        }

        final Track track = trackOpt.get();
        final String trackRawDestination = track.to();
        final URL trackDestinationURL = new URL(trackRawDestination);

        final String serviceName = trackDestinationURL.getHost();
        final RemoteService targetService = loadBalancer.choose(serviceDiscoverer.resolve(serviceName));

        final String destinationPath = trackDestinationURL.getFile();
        final URL destinationUrl = new URL(targetService.getUrl() + destinationPath);

        super.forwardRequest(track.route().method(), destinationUrl, req, res);
    }
}

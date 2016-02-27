package eu.ioservices.canopus.gateway.handling;

import eu.ioservices.canopus.gateway.routing.Track;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Optional;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class StaticRequestProcessor extends ForwardingRequestProcessor {
    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, Optional<Track> trackOpt) throws Exception {
        if (!trackOpt.isPresent()) {
            res.setStatus(HttpStatus.BAD_GATEWAY_502);
            return;
        }

        final Track track = trackOpt.get();
        final URL rawDestination = new URL(track.to());

        super.forwardRequest(track.route().method(), rawDestination, req, res);
    }
}

package eu.ioservices.canopus.gateway.handling;

import eu.ioservices.canopus.gateway.routing.Track;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface RequestProcessor {
    void process(HttpServletRequest req, HttpServletResponse res, Optional<Track> trackOpt) throws Exception;
}

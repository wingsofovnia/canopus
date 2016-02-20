package eu.ioservices.canopus.gateway.handling;

import eu.ioservices.canopus.gateway.routing.Track;
import spark.Request;
import spark.Response;

import java.util.Optional;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface RequestProcessor {
    void process(Request req, Response res, Optional<Track> trackOpt) throws Exception;
}

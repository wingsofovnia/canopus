package eu.ioservices.canopus.resolving;

import eu.ioservices.canopus.RemoteService;

import java.util.List;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface ServiceDiscoverer {
    List<RemoteService> resolve() throws ServiceDiscoveringException;

    List<RemoteService> resolve(String serviceName) throws ServiceDiscoveringException;
}

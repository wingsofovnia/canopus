package eu.ioservices.canopus.resolving;

import java.util.List;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface ServiceDiscoverer {
    List<Service> resolve() throws ServiceDiscoveringException;

    List<Service> resolve(String serviceName) throws ServiceDiscoveringException;
}

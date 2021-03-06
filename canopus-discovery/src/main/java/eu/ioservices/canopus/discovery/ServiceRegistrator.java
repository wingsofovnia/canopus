package eu.ioservices.canopus.discovery;

import eu.ioservices.canopus.Service;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface ServiceRegistrator {
    void register(Service service) throws ServiceRegistrationException;
}

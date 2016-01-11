package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public abstract class InstancesLoadBalancer implements LoadBalancer {
    protected List<RemoteService> requireValidServiceInstances(List<RemoteService> services) throws LoadBalancerException {
        if (Objects.requireNonNull(services).size() == 0)
            throw new IllegalArgumentException("List must contain at least one RemoteService instance");

        if ((int) services.stream().map(Service::getName).distinct().count() > 0)
            throw new IllegalArgumentException("List must contain only service of the same type (same name).");

        return services;
    }
}

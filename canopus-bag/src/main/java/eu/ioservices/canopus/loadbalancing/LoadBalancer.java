package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.resolving.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface LoadBalancer {
    Service choose(Collection<Service> services) throws LoadBalancerException;

    List<Service> choose(Collection<Service> services, int limit) throws LoadBalancerException;
}

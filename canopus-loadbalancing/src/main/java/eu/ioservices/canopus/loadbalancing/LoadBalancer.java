package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;

import java.util.List;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public interface LoadBalancer {
    RemoteService choose(List<RemoteService> services) throws LoadBalancerException;
}

package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.resolving.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Service choose(Collection<Service> services) throws LoadBalancerException {
        return this.choose(services, 1).get(0);
    }

    @Override
    public List<Service> choose(Collection<Service> services, int limit) throws LoadBalancerException {
        final List<Service> availableRemoteServices =
                services.stream()
                        .filter(service -> service.getStatus() != Service.Status.UNAVAILABLE)
                        .sorted((s1, s2) -> s1.getStatus().compareTo(s2.getStatus()))
                        .collect(Collectors.toList());

        Collections.shuffle(availableRemoteServices);
        return availableRemoteServices.subList(0, limit);
    }
}

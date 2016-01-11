package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class RandomLoadBalancer extends InstancesLoadBalancer {
    @Override
    public RemoteService choose(List<RemoteService> services) throws LoadBalancerException {
        final List<RemoteService> availableRemoteServices =
                this.requireValidServiceInstances(services).stream()
                        .filter(service -> service.getStatus() != RemoteService.Status.UNAVAILABLE)
                        .sorted((s1, s2) -> s1.getStatus().compareTo(s2.getStatus()))
                        .collect(Collectors.toList());

        Collections.shuffle(availableRemoteServices);
        return availableRemoteServices.get(0);
    }
}

package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public RemoteService choose(List<RemoteService> services) throws LoadBalancerException {
        if (Objects.requireNonNull(services).size() == 0)
            return null;

        if ((int) services.stream().map(Service::getName).distinct().count() > 0)
            throw new IllegalArgumentException("List must contain only service of the same type (same name).");

        final List<RemoteService> availableRemoteServices =
                services.stream()
                        .filter(service -> service.getStatus() != RemoteService.Status.UNAVAILABLE)
                        .sorted((s1, s2) -> s1.getStatus().compareTo(s2.getStatus()))
                        .collect(Collectors.toList());

        Collections.shuffle(availableRemoteServices);
        return availableRemoteServices.get(0);
    }
}

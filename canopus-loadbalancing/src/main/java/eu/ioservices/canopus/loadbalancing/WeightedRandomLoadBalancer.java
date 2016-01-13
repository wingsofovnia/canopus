package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.util.LoadBalancers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class WeightedRandomLoadBalancer extends RandomLoadBalancer {
    private final Map<String, Integer> serviceIdToWeight = new HashMap<>();

    @Override
    public RemoteService choose(List<RemoteService> services) throws LoadBalancerException {
        final Map<RemoteService, Integer> serviceToWeight
                = LoadBalancers.requireEqualNameServices(services).stream()
                    .collect(Collectors.toMap(service -> service, this::getServiceWeight));

        final int minWeight = serviceToWeight.values().stream().min(Integer::min).get();
        final List<RemoteService> theLeastLoadedServices = serviceToWeight.entrySet().stream()
                .filter(serviceToWeightEntry -> serviceToWeightEntry.getValue() == minWeight)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        final RemoteService randomLeastLoadedService = super.choose(theLeastLoadedServices);

        this.incrementServiceWeight(randomLeastLoadedService);
        return randomLeastLoadedService;
    }

    private int getServiceWeight(RemoteService remoteService) {
        return this.serviceIdToWeight.getOrDefault(Objects.requireNonNull(remoteService).getName(), 0);
    }

    private void incrementServiceWeight(RemoteService remoteService) {
        final Integer serviceWeight = this.serviceIdToWeight.getOrDefault(Objects.requireNonNull(remoteService), 0);
        serviceIdToWeight.put(remoteService.getName(), serviceWeight);
    }
}

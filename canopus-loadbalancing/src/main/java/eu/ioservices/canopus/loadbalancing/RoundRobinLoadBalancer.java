package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;

import java.util.*;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class RoundRobinLoadBalancer extends InstancesLoadBalancer {
    private final Map<String, Set<String>> serviceNameToIds = new HashMap<>();

    @Override
    public RemoteService choose(List<RemoteService> services) throws LoadBalancerException {
        for (RemoteService remoteService : this.requireValidServiceInstances(services)) {
            if (isRemembered(remoteService))
                continue;
            rememberChoice(remoteService);
            return remoteService;
        }

        final RemoteService chosenService = this.requireValidServiceInstances(services).get(0);

        final String serviceName = chosenService.getName();
        resetChoices(serviceName);
        rememberChoice(chosenService);

        return chosenService;
    }

    private boolean isRemembered(RemoteService remoteService) {
        final String serviceName = Objects.requireNonNull(remoteService).getName();
        final String serviceId = Objects.requireNonNull(remoteService).getId();

        return this.serviceNameToIds.containsKey(serviceName)
                && this.serviceNameToIds.get(serviceName).contains(serviceId);
    }

    private void rememberChoice(RemoteService remoteService) {
        final String serviceName = Objects.requireNonNull(remoteService).getName();
        final String serviceId = Objects.requireNonNull(remoteService).getId();

        if (this.serviceNameToIds.containsKey(serviceName)) {
            this.serviceNameToIds.get(serviceName).add(serviceId);
        } else {
            this.serviceNameToIds.put(serviceName, new HashSet<>(Arrays.asList(serviceId)));
        }
    }

    private void resetChoices(String serviceName) {
        this.serviceNameToIds.remove(Objects.requireNonNull(serviceName));
    }
}

package eu.ioservices.canopus.loadbalancing;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.Service;

import java.util.*;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    private final Map<String, Set<String>> serviceNameToIds = new HashMap<>();

    @Override
    public RemoteService choose(List<RemoteService> services) throws LoadBalancerException {
        if (Objects.requireNonNull(services).size() == 0)
            return null;

        if ((int) services.stream().map(Service::getName).distinct().count() > 0)
            throw new IllegalArgumentException("List must contain only service of the same type (same name).");

        for (RemoteService remoteService : services) {
            if (isRemembered(remoteService))
                continue;
            rememberChoice(remoteService);
            return remoteService;
        }

        final RemoteService chosenService = services.get(0);

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

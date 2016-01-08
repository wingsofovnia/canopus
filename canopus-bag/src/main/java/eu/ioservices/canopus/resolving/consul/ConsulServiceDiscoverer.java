package eu.ioservices.canopus.resolving.consul;

import com.ecwid.consul.v1.OperationException;
import eu.ioservices.canopus.resolving.Service;
import eu.ioservices.canopus.resolving.ServiceDiscoverer;
import eu.ioservices.canopus.resolving.ServiceDiscoveringException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class ConsulServiceDiscoverer extends ConsulDiscoveryClient implements ServiceDiscoverer {
    public ConsulServiceDiscoverer(String host) {
        super(host);
    }

    public ConsulServiceDiscoverer(String host, int port) {
        super(host, port);
    }

    @Override
    public List<Service> resolve() throws ServiceDiscoveringException {
        try {
            return consulClient.getAgentServices().getValue().values().stream()
                    .map(info -> {
                        final String serviceId = info.getId();
                        final String serviceName = info.getService();
                        final String serviceAddress = info.getAddress();
                        final Integer servicePort = info.getPort();

                        final Set<String> availableProtocols
                                = Arrays.stream(Service.Protocol.values())
                                    .map(Service.Protocol::getValue)
                                    .collect(Collectors.toSet());
                        final Service.Protocol serviceProtocol
                                = Service.Protocol.fromString(info.getTags().stream()
                                    .filter(tag -> availableProtocols.stream().anyMatch(p -> p.equals(tag)))
                                    .findFirst()
                                    .get());

                        // as of Consul 0.6.1, consul doesn't report check interval/timeout
                        final int serviceHeartbeatInterval = Service.DEFAULT_HEARTBEAT_INTERVAL;
                        final int serviceHeartbeatTimeout = Service.DEFAULT_HEARTBEAT_TIMEOUT;

                        return new Service(serviceId, serviceName, serviceAddress,
                                servicePort, serviceProtocol, serviceHeartbeatInterval, serviceHeartbeatTimeout);

                    }).collect(Collectors.toList());
        } catch (OperationException e) {
            throw new ServiceDiscoveringException("Failed to resolve services", e);
        }
    }

    @Override
    public List<Service> resolve(String serviceName) throws ServiceDiscoveringException {
        return this.resolve().stream()
                .filter(service -> Objects.requireNonNull(serviceName).equals(service.getName()))
                .collect(Collectors.toList());
    }
}
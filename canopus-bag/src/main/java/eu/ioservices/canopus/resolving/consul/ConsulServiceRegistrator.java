package eu.ioservices.canopus.resolving.consul;

import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.v1.agent.model.NewService;
import eu.ioservices.canopus.resolving.Service;
import eu.ioservices.canopus.resolving.ServiceRegistrationException;
import eu.ioservices.canopus.resolving.ServiceRegistrator;

import java.util.Arrays;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class ConsulServiceRegistrator extends ConsulDiscoveryClient implements ServiceRegistrator {
    public ConsulServiceRegistrator(String host) {
        super(host);
    }

    public ConsulServiceRegistrator(String host, int port) {
        super(host, port);
    }

    @Override
    public void register(Service service) throws ServiceRegistrationException {
        final NewService newService = new NewService() {{
            setId(service.getId());
            setAddress(service.getHost());
            setPort(service.getPort());
            setName(service.getName());
            setTags(Arrays.asList(service.getProtocol().getValue()));

            final Check httpCheck = new Check();
            httpCheck.setHttp(service.getUrl());
            httpCheck.setInterval(String.valueOf(service.getHeartbeatInterval()));
            httpCheck.setTimeout(String.valueOf(service.getHeartbeatTimeout()));

            setCheck(httpCheck);
        }};

        try {
            this.consulClient.agentServiceRegister(newService).getValue();
        } catch (OperationException e) {
            throw new ServiceRegistrationException("Failed to register new service", e);
        }
    }
}

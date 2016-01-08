package eu.ioservices.canopus.exchanger;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.exchanger.retrofit.DynamicCanopusBaseUrl;
import eu.ioservices.canopus.exchanger.retrofit.StaticCanopusBaseUrl;
import eu.ioservices.canopus.loadbalancing.LoadBalancer;
import eu.ioservices.canopus.resolving.ServiceDiscoverer;
import retrofit2.BaseUrl;
import retrofit2.Retrofit;

import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Exchanger<T> {
    private final Class<T> interfaceClass;
    private final BaseUrl baseUrlResolver;

    public Exchanger(Class<T> interfaceClass, RemoteService remoteService) {
        this.interfaceClass = Objects.requireNonNull(interfaceClass);
        this.baseUrlResolver = new StaticCanopusBaseUrl(Objects.requireNonNull(remoteService));
    }

    public Exchanger(Class<T> interfaceClass, String serviceName,
                     ServiceDiscoverer serviceDiscoverer, LoadBalancer loadBalancer) {
        this.interfaceClass = Objects.requireNonNull(interfaceClass);
        this.baseUrlResolver = new DynamicCanopusBaseUrl(Objects.requireNonNull(serviceName),
                                                         Objects.requireNonNull(serviceDiscoverer),
                                                         Objects.requireNonNull(loadBalancer));
    }

    public T create() {
        return new Retrofit.Builder()
                .baseUrl(this.baseUrlResolver)
                .build()
                .create(interfaceClass);
    }

    public static final class Interaction {
        private RemoteService remoteService;
        private String serviceName;
        private ServiceDiscoverer serviceDiscoverer;
        private LoadBalancer loadBalancer;

        public Interaction with(RemoteService remoteService) {
            this.remoteService = Objects.requireNonNull(remoteService);
            return this;
        }

        public Interaction with(String serviceName) {
            this.serviceName = Objects.requireNonNull(serviceName);
            return this;
        }

        public Interaction using(ServiceDiscoverer serviceDiscoverer) {
            this.serviceDiscoverer = Objects.requireNonNull(serviceDiscoverer);
            return this;
        }

        public Interaction using(LoadBalancer loadBalancer) {
            this.loadBalancer = Objects.requireNonNull(loadBalancer);
            return this;
        }

        public <T> T via(Class<T> interfaceClass) {
            Objects.requireNonNull(interfaceClass);

            if (this.serviceName != null) {
                if (serviceDiscoverer == null || loadBalancer == null)
                    throw new IllegalStateException("serviceDiscoverer and loadBalancer must be set, " +
                            "if dynamic resolving by serviceName is selected");

                return new Exchanger<>(interfaceClass, serviceName, serviceDiscoverer, loadBalancer).create();
            } else if (this.remoteService != null) {
                return new Exchanger<>(interfaceClass, remoteService).create();
            } else {
                throw new IllegalStateException("Exchanger can not be built from nothing. " +
                        "ServiceName or remoteService must be specified!");
            }
        }
    }
}

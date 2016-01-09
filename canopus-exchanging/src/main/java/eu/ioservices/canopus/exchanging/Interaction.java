package eu.ioservices.canopus.exchanging;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.exchanging.netflix.feign.CanopusHttpClient;
import eu.ioservices.canopus.exchanging.netflix.feign.CanopusInvocationHandler;
import eu.ioservices.canopus.loadbalancing.LoadBalancer;
import eu.ioservices.canopus.discovery.ServiceDiscoverer;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

import java.net.URL;
import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Interaction {
    public static final String FEIGN_TARGET_BASE_URL = "{baseurl}";
    private static final Decoder DEFAULT_DECODER = new GsonDecoder();
    private static final Encoder DEFAULT_ENCODER = new GsonEncoder();

    private final Feign.Builder feignBuilder = new Feign.Builder();
    private Decoder decoder = DEFAULT_DECODER;
    private Encoder encoder = DEFAULT_ENCODER;
    private ServiceDiscoverer serviceDiscoverer;
    private LoadBalancer loadBalancer;
    private RemoteService remoteService;
    private String serviceName;
    private String url;

    public Interaction with(RemoteService remoteService) {
        this.remoteService = Objects.requireNonNull(remoteService);
        return this;
    }

    public Interaction with(String serviceNameOrUrl) {
        Objects.requireNonNull(serviceNameOrUrl);
        try {
            new URL(serviceNameOrUrl).toURI();
            this.url = serviceNameOrUrl;
        } catch (Exception e) {
            this.serviceName = serviceNameOrUrl;
        }
        return this;
    }

    public Interaction with(Decoder decoder) {
        this.decoder = Objects.requireNonNull(decoder);
        return this;
    }

    public Interaction with(Encoder encoder) {
        this.encoder = Objects.requireNonNull(encoder);
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
        CanopusHttpClient httpClient;
        if (this.serviceName != null) {
            if (serviceDiscoverer == null || loadBalancer == null)
                throw new IllegalStateException("serviceDiscoverer and loadBalancer must be set, " +
                        "if dynamic resolving by serviceName is selected");

            httpClient = new CanopusHttpClient(serviceName, serviceDiscoverer, loadBalancer);
        } else if (this.remoteService != null) {
            httpClient = new CanopusHttpClient(remoteService);
        } else if (this.url != null) {
            httpClient = new CanopusHttpClient(url);
        } else
            throw new IllegalStateException("Feign can not be built from nothing. " +
                    "ServiceName or remoteService must be specified!");

        return feignBuilder.client(httpClient)
                           .encoder(this.encoder)
                           .decoder(this.decoder)
                           .invocationHandlerFactory(CanopusInvocationHandler::new)
                           .target(interfaceClass, FEIGN_TARGET_BASE_URL);
    }
}

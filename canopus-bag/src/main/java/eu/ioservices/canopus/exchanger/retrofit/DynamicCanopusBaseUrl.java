package eu.ioservices.canopus.exchanger.retrofit;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.loadbalancing.LoadBalancer;
import eu.ioservices.canopus.resolving.ServiceDiscoverer;
import okhttp3.HttpUrl;
import retrofit2.BaseUrl;

import java.util.List;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class DynamicCanopusBaseUrl implements BaseUrl {
    private final String serviceName;
    private final ServiceDiscoverer serviceDiscoverer;
    private final LoadBalancer loadBalancer;

    public DynamicCanopusBaseUrl(String serviceName, ServiceDiscoverer serviceDiscoverer, LoadBalancer loadBalancer) {
        this.serviceName = serviceName;
        this.serviceDiscoverer = serviceDiscoverer;
        this.loadBalancer = loadBalancer;
    }

    @Override
    public HttpUrl url() {
        final List<RemoteService> remoteServices = serviceDiscoverer.resolve(serviceName);
        final RemoteService balancedRemoteService = loadBalancer.choose(remoteServices);
        final String urlAddress = balancedRemoteService.getUrl();
        return HttpUrl.parse(urlAddress);
    }
}

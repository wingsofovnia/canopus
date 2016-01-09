package eu.ioservices.canopus.exchanging.netflix.feign;

import eu.ioservices.canopus.RemoteService;
import eu.ioservices.canopus.exchanging.Interaction;
import eu.ioservices.canopus.loadbalancing.LoadBalancer;
import eu.ioservices.canopus.discovery.ServiceDiscoverer;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.okhttp.OkHttpClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class CanopusHttpClient implements Client {
    private static interface ServicePathResolver {
        String url();
    }

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final ServicePathResolver servicePathResolver;

    public CanopusHttpClient(String baseUrl) {
        this.servicePathResolver = () -> Objects.requireNonNull(baseUrl);
    }

    public CanopusHttpClient(RemoteService remoteService) {
        this.servicePathResolver = Objects.requireNonNull(remoteService)::getUrl;
    }

    public CanopusHttpClient(String serviceName, ServiceDiscoverer serviceDiscoverer, LoadBalancer loadBalancer) {
        Objects.requireNonNull(serviceName);
        Objects.requireNonNull(serviceDiscoverer);
        Objects.requireNonNull(loadBalancer);

        this.servicePathResolver = () -> {
            final List<RemoteService> remoteServices = serviceDiscoverer.resolve(serviceName);
            final RemoteService balancedRemoteService = loadBalancer.choose(remoteServices);
            return balancedRemoteService.getUrl();
        };
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        final String method = request.method();
        final Map<String, Collection<String>> headers = request.headers();
        final Charset charset = request.charset();
        final byte[] body = request.body();
        final String url = request.url().replaceFirst(Pattern.quote(Interaction.FEIGN_TARGET_BASE_URL),
                                                                    servicePathResolver.url());

        final Request reqWithResolvedService = Request.create(method, url, headers, body, charset);
        return this.okHttpClient.execute(reqWithResolvedService, options);
    }
}

package eu.ioservices.canopus.exchanger.retrofit;

import eu.ioservices.canopus.RemoteService;
import okhttp3.HttpUrl;
import retrofit2.BaseUrl;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class StaticCanopusBaseUrl implements BaseUrl {
    private final RemoteService remoteService;

    public StaticCanopusBaseUrl(RemoteService remoteService) {
        this.remoteService = remoteService;
    }

    @Override
    public HttpUrl url() {
        return HttpUrl.parse(remoteService.getUrl());
    }
}

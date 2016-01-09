package eu.ioservices.canopus.discovery.consul;

import com.ecwid.consul.v1.ConsulClient;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
abstract class ConsulDiscoveryClient {
    final ConsulClient consulClient;

    ConsulDiscoveryClient(String host) {
        this.consulClient = new ConsulClient(host);
    }

    ConsulDiscoveryClient(String host, int port) {
        this.consulClient = new ConsulClient(host, port);
    }
}

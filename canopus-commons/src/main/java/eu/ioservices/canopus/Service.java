package eu.ioservices.canopus;

import eu.ioservices.canopus.utils.IPUtils;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class Service {
    public static enum Protocol {
        HTTP("http"), HTTPS("https");

        private final String value;

        Protocol(String protocol) {
            this.value = protocol;
        }

        public String getValue() {
            return value;
        }

        public static Protocol fromString(String text) {
            for (Protocol p : Protocol.values()) {
                if (Objects.requireNonNull(text).equalsIgnoreCase(p.value))
                    return p;
            }
            return null;
        }
    }

    public static final int DEFAULT_HEARTBEAT_INTERVAL = 60;
    public static final int DEFAULT_HEARTBEAT_TIMEOUT = 5;
    public static final Protocol DEFAULT_PROTOCOL = Protocol.HTTP;
    private static final String ID_PATTERN = "%s@%s:%s@:%s";
    private static final String URL_PATTERN = "%s://%s:%d";

    private final String id;
    private final String name;
    private final String host;
    private final int port;
    private final Protocol protocol;
    private final int heartbeatInterval;
    private final int heartbeatTimeout;

    public Service(String host, int port, String name) {
        this(host, port, name, DEFAULT_PROTOCOL);
    }

    public Service(int port, String name) throws SocketException, UnknownHostException {
        this(IPUtils.getPrivateAddress(), port, name, DEFAULT_PROTOCOL);
    }

    public Service(String host, int port, String name, Protocol protocol) {
        this.id = String.format(ID_PATTERN, name, host, port, System.nanoTime());
        this.host = Objects.requireNonNull(host);
        this.port = IPUtils.requireValidPort(port);
        this.name = Objects.requireNonNull(name);
        this.protocol = Objects.requireNonNull(protocol);
        this.heartbeatInterval = DEFAULT_HEARTBEAT_INTERVAL;
        this.heartbeatTimeout = DEFAULT_HEARTBEAT_TIMEOUT;
    }

    public Service(int port, String name, Protocol protocol) throws SocketException, UnknownHostException {
        this(IPUtils.getPrivateAddress(), port, name, protocol);
    }

    public Service(String id, String name, String host, int port, Protocol protocol, int heartbeatInterval,
                   int heartbeatTimeout) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.host = Objects.requireNonNull(host);
        this.port = IPUtils.requireValidPort(port);
        this.protocol = Objects.requireNonNull(protocol);
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public Service(String id, String name, int port, Protocol protocol, int heartbeatInterval,
                   int heartbeatTimeout) throws SocketException, UnknownHostException {
        this(id, name, IPUtils.getPrivateAddress(), port, protocol, heartbeatInterval, heartbeatTimeout);
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getUrl() {
        return String.format(URL_PATTERN, protocol.value, host, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Service))
            return false;

        Service service = (Service) o;
        return heartbeatInterval == service.heartbeatInterval
                && heartbeatTimeout == service.heartbeatTimeout
                && port == service.port && host.equals(service.host)
                && id.equals(service.id) && name.equals(service.name)
                && protocol == service.protocol;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + protocol.hashCode();
        result = 31 * result + heartbeatInterval;
        result = 31 * result + heartbeatTimeout;
        return result;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", protocol=" + protocol +
                ", heartbeatInterval=" + heartbeatInterval +
                ", heartbeatTimeout=" + heartbeatTimeout +
                '}';
    }
}

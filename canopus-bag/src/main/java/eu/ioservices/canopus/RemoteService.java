package eu.ioservices.canopus;

import java.util.Objects;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class RemoteService extends Service {
    public static enum Status {
        UNKNOWN, UNAVAILABLE, WARNING, OK
    }
    private final Status status;

    public RemoteService(String id, String name, String host, int port, Protocol protocol, int heartbeatInterval,
                         int heartbeatTimeout, Status status) {
        super(id, name, host, port, protocol, heartbeatInterval, heartbeatTimeout);
        this.status = Objects.requireNonNull(status);
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RemoteService))
            return false;
        if (!super.equals(o))
            return false;

        RemoteService that = (RemoteService) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RemoteService{" +
                "id='" + super.getId() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", host='" + super.getHost() + '\'' +
                ", port=" + super.getPort() +
                ", protocol=" + super.getProtocol() +
                ", heartbeatInterval=" + super.getHeartbeatInterval() +
                ", heartbeatTimeout=" + super.getHeartbeatTimeout() +
                ", status=" + status +
                '}';
    }
}

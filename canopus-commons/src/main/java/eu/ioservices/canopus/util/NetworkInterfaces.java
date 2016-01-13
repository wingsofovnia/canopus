package eu.ioservices.canopus.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public final class NetworkInterfaces {
    private NetworkInterfaces() {
        throw new AssertionError("No eu.ioservices.canopus.util.NetworkInterfaces instances for you!");
    }

    public static boolean isPrivateAddress(String ipAddress) throws UnknownHostException {
        InetAddress addressParsedByName = InetAddress.getByName(ipAddress);
        InetAddress addressParsedByIP = InetAddress.getByAddress(addressParsedByName.getAddress());
        return addressParsedByIP.isSiteLocalAddress();
    }

    public static List<NetworkInterface> getNetworkInterfaces(boolean isUp) throws SocketException {
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (Iterator<NetworkInterface> iterator = interfaces.iterator(); iterator.hasNext();) {
            NetworkInterface netInt = iterator.next();
            if (netInt.isUp() != isUp)
                iterator.remove();
        }

        return interfaces;
    }

    public static List<String> getPrivateAddresses() throws SocketException {
        List<String> privateIpAddresses = new ArrayList<>();
        for (NetworkInterface netInterface : NetworkInterfaces.getNetworkInterfaces(true)) {
            for (Enumeration<InetAddress> netAddress = netInterface.getInetAddresses(); netAddress.hasMoreElements();)
                privateIpAddresses.add(netAddress.nextElement().getHostAddress());
        }
        return privateIpAddresses;
    }


    public static String getPrivateAddress() throws SocketException, UnknownHostException {
        final List<String> privateIPAddresses = NetworkInterfaces.getPrivateAddresses();
        if (privateIPAddresses.size() > 1)
            throw new UnknownHostException("Failed to determine private IP address - multiple found: "
                    + privateIPAddresses.toString());
        return privateIPAddresses.get(0);
    }

    public static int requireValidPort(int port) {
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Port must be in 0-65536 range");
        return port;
    }
}

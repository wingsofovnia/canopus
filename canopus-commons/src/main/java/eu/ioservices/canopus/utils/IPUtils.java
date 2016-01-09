package eu.ioservices.canopus.utils;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author &lt;<a href="mailto:illia.ovchynnikov@gmail.com">illia.ovchynnikov@gmail.com</a>&gt;
 */
public class IPUtils {
    public static boolean isPrivateAddress(String ipAddress) throws UnknownHostException {
        InetAddress addressParsedByName = InetAddress.getByName(ipAddress);
        InetAddress addressParsedByIP = InetAddress.getByAddress(addressParsedByName.getAddress());
        return addressParsedByIP.isSiteLocalAddress();
    }

    public static List<String> getPrivateAddresses() throws SocketException {
        List<String> privateIpAddresses = new ArrayList<>();
        for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
             interfaces.hasMoreElements(); ) {
            for (Enumeration<InetAddress> inetAddress = interfaces.nextElement().getInetAddresses();
                 inetAddress.hasMoreElements(); ) {
                privateIpAddresses.add(inetAddress.nextElement().getHostAddress());
            }
        }
        return privateIpAddresses;
    }


    public static String getPrivateAddress() throws SocketException, UnknownHostException {
        final List<String> privateIPAddresses = IPUtils.getPrivateAddresses();
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

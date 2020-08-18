package wcyoung.http.loadbalancer.util;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public final class ChannelUtil {

    public static String getRemoteIp(Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    public static int getRemotePort(Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getPort();
    }

    public static String getRemoteAddress(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    public static String getLocalIp(Channel channel) {
        return ((InetSocketAddress) channel.localAddress()).getAddress().getHostAddress();
    }

    public static int getLocalPort(Channel channel) {
        return ((InetSocketAddress) channel.localAddress()).getPort();
    }

    public static String getLocalAddress(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.localAddress();
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}

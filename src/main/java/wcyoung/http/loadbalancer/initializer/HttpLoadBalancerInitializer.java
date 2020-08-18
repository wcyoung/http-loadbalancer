package wcyoung.http.loadbalancer.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import wcyoung.http.loadbalancer.handler.HttpProxyClientHandler;
import wcyoung.http.loadbalancer.remotes.RemoteServers;

public class HttpLoadBalancerInitializer extends ChannelInitializer<SocketChannel> {

    private final RemoteServers servers;

    public HttpLoadBalancerInitializer(RemoteServers servers) {
        this.servers = servers;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("codec", new HttpServerCodec())
                .addLast("handler", new HttpProxyClientHandler(servers.get()));
    }

}

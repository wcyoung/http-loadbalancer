package wcyoung.http.loadbalancer.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import wcyoung.http.loadbalancer.handler.HttpProxyClientHandler;
import wcyoung.http.loadbalancer.remotes.RemoteServerSupplier;

public class HttpLoadBalancerInitializer extends ChannelInitializer<SocketChannel> {

    private final RemoteServerSupplier servers;

    public HttpLoadBalancerInitializer(RemoteServerSupplier servers) {
        this.servers = servers;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("codec", new HttpServerCodec())
                .addLast("handler", new HttpProxyClientHandler(servers.get(pipeline.channel())));
    }

}

package wcyoung.http.loadbalancer.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import wcyoung.http.loadbalancer.handler.HttpProxyClientHandler;

public class HttpLoadBalancerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final String remoteHost = "localhost";
        final int remotePort = 8000;

        ch.pipeline().addLast("codec", new HttpServerCodec())
                .addLast("handler", new HttpProxyClientHandler(remoteHost, remotePort));
    }

}

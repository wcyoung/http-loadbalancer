package wcyoung.http.loadbalancer.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import wcyoung.http.loadbalancer.handler.HttpProxyClientHandler;
import wcyoung.http.loadbalancer.remotes.RemoteServer;

public class HttpLoadBalancerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        RemoteServer remoteServer = new RemoteServer("localhost", 8000);

        ch.pipeline().addLast("codec", new HttpServerCodec())
                .addLast("handler", new HttpProxyClientHandler(remoteServer));
    }

}

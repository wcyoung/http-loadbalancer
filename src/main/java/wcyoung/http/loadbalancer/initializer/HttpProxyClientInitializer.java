package wcyoung.http.loadbalancer.initializer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import wcyoung.http.loadbalancer.handler.HttpProxyServerHandler;

public class HttpProxyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel inboundChannel;

    public HttpProxyClientInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("codec", new HttpClientCodec())
                .addLast("handler", new HttpProxyServerHandler(inboundChannel));
    }

}

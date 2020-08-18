package wcyoung.http.loadbalancer.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wcyoung.http.loadbalancer.initializer.HttpProxyClientInitializer;
import wcyoung.http.loadbalancer.remotes.RemoteServer;
import wcyoung.http.loadbalancer.util.ChannelUtil;

public class HttpProxyClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RemoteServer remoteServer;

    private Channel outboundChannel;

    public HttpProxyClientHandler(RemoteServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .handler(new HttpProxyClientInitializer(inboundChannel))
                .option(ChannelOption.AUTO_READ, false);

        ChannelFuture clientFuture = bootstrap.connect(remoteServer.host(), remoteServer.port());
        outboundChannel = clientFuture.channel();

        log.info("[{}] -> [{}] -> [{}]",
                ChannelUtil.getRemoteAddress(inboundChannel),
                ChannelUtil.getLocalAddress(inboundChannel),
                remoteServer.address());

        clientFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                inboundChannel.read();
            } else {
                inboundChannel.close();
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception: {}", ExceptionUtils.getStackTrace(cause));
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}

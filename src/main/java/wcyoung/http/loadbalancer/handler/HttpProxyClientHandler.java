package wcyoung.http.loadbalancer.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;
import org.apache.commons.lang3.StringUtils;
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
            if (msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;
                replaceHttpHeaders(request.headers(), ctx.channel());
            }
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

    private void replaceHttpHeaders(HttpHeaders headers, Channel inboundChannel) {
        String originalAddress = headers.get(HttpHeaderNames.HOST);
        String replaceAddress = remoteServer.address();
        AsciiString[] replaceHeaderNames = {HttpHeaderNames.HOST, HttpHeaderNames.ORIGIN, HttpHeaderNames.REFERER};

        String headerValue;
        for (AsciiString headerName : replaceHeaderNames) {
            headerValue = headers.get(headerName);
            if (StringUtils.isNotBlank(headerValue)) {
                headers.set(headerName, headerValue.replace(originalAddress, replaceAddress));
            }
        }

        String headerName = "X-Forwarded-For";
        headerValue = headers.get(headerName);
        if (StringUtils.isNotBlank(headerValue)) {
            headers.set(headerName, headerValue + ", " + ChannelUtil.getLocalIp(inboundChannel));
        } else {
            headers.add(headerName, ChannelUtil.getRemoteIp(inboundChannel));

            headerName = "X-Real-IP";
            headerValue = headers.get(headerName);
            if (StringUtils.isBlank(headerValue)) {
                headers.add(headerName, ChannelUtil.getRemoteIp(inboundChannel));
            }
        }
    }

}

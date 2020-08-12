package wcyoung.http.loadbalancer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wcyoung.http.loadbalancer.initializer.HttpLoadBalancerInitializer;

public class HttpLoadBalancer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        new HttpLoadBalancer().start();
    }

    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpLoadBalancerInitializer())
                    .childOption(ChannelOption.AUTO_READ, false);

            ChannelFuture future = bootstrap.bind(8888).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Exception: {}", ExceptionUtils.getStackTrace(e));
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

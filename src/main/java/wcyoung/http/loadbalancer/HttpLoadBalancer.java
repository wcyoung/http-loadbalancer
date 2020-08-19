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
import wcyoung.http.loadbalancer.remotes.RemoteServerHashingSupplier;
import wcyoung.http.loadbalancer.remotes.RemoteServer;
import wcyoung.http.loadbalancer.remotes.RemoteServerRoundRobinSupplier;
import wcyoung.http.loadbalancer.remotes.RemoteServerSupplier;

import java.util.ArrayList;
import java.util.List;

public class HttpLoadBalancer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        List<RemoteServer> serverList = new ArrayList<>();
        serverList.add(new RemoteServer("localhost", 8000));
        serverList.add(new RemoteServer("localhost", 8001));

        //RemoteServerSupplier servers = new RemoteServerRoundRobinSupplier(serverList);
        RemoteServerSupplier servers = new RemoteServerHashingSupplier(serverList);

        new HttpLoadBalancer().start(servers);
    }

    private void start(RemoteServerSupplier servers) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpLoadBalancerInitializer(servers))
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

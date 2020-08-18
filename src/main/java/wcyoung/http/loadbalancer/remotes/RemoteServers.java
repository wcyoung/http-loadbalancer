package wcyoung.http.loadbalancer.remotes;

import io.netty.channel.Channel;

public interface RemoteServers {

    RemoteServer get(Channel inboundChannel);

}

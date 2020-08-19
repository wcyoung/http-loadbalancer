package wcyoung.http.loadbalancer.remotes;

import io.netty.channel.Channel;

public interface RemoteServerSupplier {

    RemoteServer get(Channel inboundChannel);

}

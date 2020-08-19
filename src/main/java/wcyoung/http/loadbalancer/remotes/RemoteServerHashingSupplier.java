package wcyoung.http.loadbalancer.remotes;

import io.netty.channel.Channel;
import wcyoung.http.loadbalancer.util.ChannelUtil;

import java.util.List;

public class RemoteServerHashingSupplier extends AbstractRemoteServerSupplier {

    public RemoteServerHashingSupplier(List<RemoteServer> serverList) {
        super(serverList);
    }

    @Override
    public RemoteServer get(Channel inboundChannel) {
        String remoteAddress = ChannelUtil.getRemoteAddress(inboundChannel);
        byte[] bytes = remoteAddress.getBytes();

        int hashingInt = 0;
        for (byte b : bytes) {
            hashingInt += b;
        }

        return servers.get(hashingInt % serverCount);
    }

}

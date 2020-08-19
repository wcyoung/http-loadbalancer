package wcyoung.http.loadbalancer.remotes;

import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteServerRoundRobinSupplier extends AbstractRemoteServerSupplier {

    private AtomicInteger atomicIndex;

    public RemoteServerRoundRobinSupplier(List<RemoteServer> serverList) {
        super(serverList);
        this.atomicIndex = new AtomicInteger(-1);
    }

    @Override
    public RemoteServer get(Channel inboundChannel) {
        int index = atomicIndex.incrementAndGet();
        if (index == Integer.MAX_VALUE) {
            index = atomicIndex.updateAndGet(x -> 0);
        }
        return servers.get(index % serverCount);
    }

}

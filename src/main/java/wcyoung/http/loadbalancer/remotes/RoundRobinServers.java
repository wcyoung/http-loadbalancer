package wcyoung.http.loadbalancer.remotes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinServers {

    private final List<RemoteServer> servers;
    private final int serverCount;

    private AtomicInteger atomicIndex;

    public RoundRobinServers(List<RemoteServer> servers) {
        this.servers = servers;
        this.serverCount = servers.size();
        this.atomicIndex = new AtomicInteger(-1);
    }

    public RemoteServer get() {
        int index = atomicIndex.incrementAndGet();
        if (index == Integer.MAX_VALUE) {
            index = atomicIndex.updateAndGet(x -> 0);
        }
        return servers.get(index % serverCount);
    }

}

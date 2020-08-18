package wcyoung.http.loadbalancer.remotes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinServers implements RemoteServers {

    private List<RemoteServer> servers;
    private int serverCount;

    private AtomicInteger atomicIndex;

    public RoundRobinServers(List<RemoteServer> serverList) {
        this.servers = serverList;
        this.serverCount = serverList.size();
        this.atomicIndex = new AtomicInteger(-1);
    }

    @Override
    public RemoteServer get() {
        int index = atomicIndex.incrementAndGet();
        if (index == Integer.MAX_VALUE) {
            index = atomicIndex.updateAndGet(x -> 0);
        }
        return servers.get(index % serverCount);
    }

}

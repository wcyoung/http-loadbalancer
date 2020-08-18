package wcyoung.http.loadbalancer.remotes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinServers implements RemoteServers {

    private static final RoundRobinServers INSTANCE = new RoundRobinServers();

    private List<RemoteServer> servers;
    private int serverCount;

    private AtomicInteger atomicIndex;

    private RoundRobinServers() {}

    public static RemoteServers getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean loadServers(List<RemoteServer> serverList) {
        if (serverList == null || serverList.size() < 1) {
            return false;
        }

        this.servers = serverList;
        this.serverCount = serverList.size();
        this.atomicIndex = new AtomicInteger(-1);

        return true;
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

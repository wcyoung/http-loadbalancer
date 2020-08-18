package wcyoung.http.loadbalancer.remotes;

import java.util.List;

public abstract class AbstractRemoteServers implements RemoteServers {

    protected List<RemoteServer> servers;
    protected int serverCount;

    public AbstractRemoteServers(List<RemoteServer> serverList) {
        this.servers = serverList;
        this.serverCount = serverList.size();
    }

}

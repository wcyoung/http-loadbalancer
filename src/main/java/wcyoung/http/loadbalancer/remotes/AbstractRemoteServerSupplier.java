package wcyoung.http.loadbalancer.remotes;

import java.util.List;

public abstract class AbstractRemoteServerSupplier implements RemoteServerSupplier {

    protected List<RemoteServer> servers;
    protected int serverCount;

    public AbstractRemoteServerSupplier(List<RemoteServer> serverList) {
        this.servers = serverList;
        this.serverCount = serverList.size();
    }

}

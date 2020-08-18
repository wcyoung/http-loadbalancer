package wcyoung.http.loadbalancer.remotes;

import java.util.List;

public interface RemoteServers {

    boolean loadServers(List<RemoteServer> serverList);

    RemoteServer get();

}

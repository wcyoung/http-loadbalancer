package wcyoung.http.loadbalancer.remotes;

public class RemoteServer {

    private final String host;
    private final int port;
    private final String address;

    public RemoteServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.address = host + ":" + port;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String address() {
        return address;
    }

}

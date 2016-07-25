import java.net.InetSocketAddress;


public interface GossipUpdater {
	
	void update(InetSocketAddress address);
}

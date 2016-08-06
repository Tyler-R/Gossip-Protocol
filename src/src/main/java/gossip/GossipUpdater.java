package src.main.java.gossip;
import java.net.InetSocketAddress;


public interface GossipUpdater {
	
	void update(InetSocketAddress address);
}

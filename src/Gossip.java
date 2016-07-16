import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Gossip {
	
	public int listeningPort = 8080;
	
	private Network network;
	
	private Member self = null;
	private HashMap<String, Member> memberList = new HashMap<String, Member>();
	
	private int peersToUpdatePerInterval = 3; 
	
	
	/**
	 * initialize gossip protocol as the first node in the system.
	 * */
	public Gossip(String localIpAddress, int listeningPort) {
		this.listeningPort = listeningPort;
		this.network = new Network(listeningPort);
		
		self = new Member(localIpAddress, listeningPort, 0);
		memberList.put(self.getUniqueId(), self);
	}
	
	/**
	 * Connect to another node in the gossip protocol and 
	 * begin fault tolerance monitoring.
	 * */
	public Gossip(String localIpAddress, int listeningPort, String ipAddress, int port) {
		this(localIpAddress, listeningPort);
		
		Member initialTarget = new Member(ipAddress, port, 0);
		memberList.put(initialTarget.getUniqueId(), initialTarget);
		
		Member x = new Member("127.0.0.1", 8082, 0);
		memberList.put(x.getUniqueId(), x);
		
		Member y = new Member("127.0.0.1", 8083, 0);
		memberList.put(y.getUniqueId(), y);
	}
	
	public void start() {
		new Thread(() -> {
			network.sendMessage(self, "hello world");
			sendMemberListToRandomMemeber();
			
			System.out.println("received message: " + network.receiveMessage());
			
			
			System.out.println("received message: " + network.receiveMessage());
		}).start();
	}
	
	private void sendMemberListToRandomMemeber() {
		self.incremenetSequenceNumber();
		
		List<String> peersToUpdate = new ArrayList<String>();
		Object[] keys = memberList.keySet().toArray();
		
		if (keys.length < peersToUpdatePerInterval) {
			for (int i = 0; i < keys.length; i++) {
				String key = (String) keys[i];
				if (!key.equals(self.getUniqueId())) {
					peersToUpdate.add(key);
				}
			}
		} else {
			for (int i = 0; i < peersToUpdatePerInterval; i++) {
				boolean newTargetFound = false;
				
				while(!newTargetFound) {
					int randomIndex = (int) (Math.random() * memberList.size());
				
					String targetKey = (String) keys[randomIndex];
					
					if(!targetKey.equals(self.getUniqueId())) {
						newTargetFound = true;
						peersToUpdate.add(targetKey);
					}
				}
			}
		}
		
		
		
		for (String targetKey : peersToUpdate) {
			System.out.println("-" + targetKey);
			Member target = memberList.get(targetKey);
			network.sendMessage(target, target.getNetworkMessage());
		}
	}
	
	
	
}

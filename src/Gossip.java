import java.util.HashMap;


public class Gossip {
	
	public int listeningPort = 8080;
	
	private Network network;
	
	Member self = null;
	private HashMap<String, Member> memberList = new HashMap<String, Member>();
	
	
	/**
	 * initialize gossip protocol as the first node in the system.
	 * */
	public Gossip(int listeningPort) {
		this.listeningPort = listeningPort;
		this.network = new Network(listeningPort);
	}
	
	/**
	 * Connect to another node in the gossip protocol and 
	 * begin fault tolerance monitoring.
	 * */
	public Gossip(int listeningPort, String ipAddress, int port) {
		this(listeningPort);
		
		self = new Member(ipAddress, port, 0);
		memberList.put(self.getUniqueId(), self);
	}
	
	void start() {
		new Thread(() -> {
			network.sendMessage(self, "hello world");
			System.out.println("received message: " + network.receiveMessage());
		}).start();
	}
	
}

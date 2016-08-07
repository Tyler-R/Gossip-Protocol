package main.java.gossip;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



public class Gossip {
	
	public int listeningPort = 8080;
	
	private Network network;
	
	private Member self = null;
	private ConcurrentHashMap<String, Member> memberList = new ConcurrentHashMap<String, Member>();
	
	// configurable values
	Config config = null;
	
	private GossipUpdater onNewMember = null;
	private GossipUpdater onFailedMember = null;
	private GossipUpdater onRemovedMember = null;
	private GossipUpdater onRevivedMember = null;
	
	private Thread sendThread = null;
	private Thread receiveThread = null;
	private Thread failureDetectionThread = null;
	
	/**
	 * initialize gossip protocol as the first node in the system.
	 * */
	public Gossip(String localIpAddress, int listeningPort, Config config) {
		this.config = config;
		
		this.listeningPort = listeningPort;
		this.network = new Network(listeningPort);
		
		self = new Member(localIpAddress, listeningPort, 0, config);
		memberList.put(self.getUniqueId(), self);
	}
	
	/**
	 * Connect to another node in the gossip protocol and 
	 * begin fault tolerance monitoring.
	 * */
	public Gossip(String localIpAddress, int listeningPort, String ipAddress, int port, Config config) {
		this(localIpAddress, listeningPort, config);
		
		Member initialTarget = new Member(ipAddress, port, 0, config);
		memberList.put(initialTarget.getUniqueId(), initialTarget);
	}
	
	public void start() {
		startSendThread();
		startReceiveThread();
		startFailureDetectionThread();
	}
	
	private void startSendThread() {
		sendThread = new Thread(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(config.UPDATE_FREQUENCY);
				} catch(InterruptedException e){
			        Thread.currentThread().interrupt();
			        break;
			    } catch (Exception e) {
					e.printStackTrace();
				}
				
				sendMemberListToRandomMemeber();
			}
		});
		
		sendThread.start();
	}
	
	private void startReceiveThread() {
		receiveThread = new Thread(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				receiveMemberList();
			}
		});
		
		receiveThread.start();
	}
	
	private void startFailureDetectionThread() {
		failureDetectionThread = new Thread(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				detectFailedMembers();
				try {
					Thread.sleep(config.FAILURE_DETECTION_FREQUENCY);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		failureDetectionThread.start();
	}
	
	public void stop() {
		sendThread.interrupt();
		receiveThread.interrupt();
		failureDetectionThread.interrupt();
	}
	
	private void detectFailedMembers() {
		String[] keys = new String[memberList.size()];
		memberList.keySet().toArray(keys);
				
		for (String key : keys) {
			Member member = memberList.get(key);
			
			boolean hadFailed = member.hasFailed();
			
			member.checkIfFailed();
			
			// node failure status has changed
			if (member.hasFailed() != hadFailed) {
				if (member.hasFailed()) {
					if(onFailedMember != null) {
						onFailedMember.update(member.getSocketAddress());
					}
				} else {
					if(onRevivedMember != null) {
						onRevivedMember.update(member.getSocketAddress());
					}
				}
			}
			
			if(member.shouldCleanup()) {
				synchronized(memberList) {
					memberList.remove(key);
					
					if (onRemovedMember != null) {
						onRemovedMember.update(member.getSocketAddress());
					}
				}
			}
		}
	}
	
	private void receiveMemberList() {
		Member newMember = network.receiveMessage();
		System.out.println(self.getNetworkMessage() + " - Received: " + newMember.getNetworkMessage());
		
		Member member = memberList.get(newMember.getUniqueId());
		if (member == null) { // member not in the list
			synchronized(memberList) {
				newMember.setConfig(config);
				newMember.updateLastUpdateTime();
				
				memberList.put(newMember.getUniqueId(), newMember);
				
				if (onNewMember != null) {
					onNewMember.update(newMember.getSocketAddress());
				}
			}
		} else { // member was in the list
			member.updateSequenceNumber(newMember.getSequenceNumber());
		}
	}
	
	private void sendMemberListToRandomMemeber() {
		self.incremenetSequenceNumber();
		
		List<String> peersToUpdate = new ArrayList<String>();
		Object[] keys = memberList.keySet().toArray();
		
		if (keys.length < config.PEERS_TO_UPDATE_PER_INTERVAL) {
			for (int i = 0; i < keys.length; i++) {
				String key = (String) keys[i];
				if (!key.equals(self.getUniqueId())) {
					peersToUpdate.add(key);
				}
			}
		} else {
			for (int i = 0; i < config.PEERS_TO_UPDATE_PER_INTERVAL; i++) {
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
			Member target = memberList.get(targetKey);
			System.out.println(self.getNetworkMessage() + " - sending to: " + memberList.get(targetKey).getNetworkMessage());
			
			for(Member member : memberList.values()) {
				network.sendMessage(target, member);
			}
		}
	}
	
	public ArrayList<InetSocketAddress> getAliveMembers() {
		// assume that most members are alive in the list and so 
		// we set the initial size of the list to return to prevent arrayList resizing.
		int initialSize = memberList.size();
		ArrayList<InetSocketAddress> aliveMembers = new ArrayList<InetSocketAddress>(initialSize);
		
		
		for (String key : memberList.keySet()) {
			Member member = memberList.get(key);
			if (!member.hasFailed()) {
				String ipAddress = member.getAddress();
				int port = member.getPort();
				
				aliveMembers.add(new InetSocketAddress(ipAddress, port));
			}
		}
		
		return aliveMembers;
	}
	
	public ArrayList<InetSocketAddress> getFailedMembers() {
		ArrayList<InetSocketAddress> failedMembers = new ArrayList<InetSocketAddress>();
		
		for (String key : memberList.keySet()) {
			Member member = memberList.get(key);
			
			member.checkIfFailed();
			
			if (member.hasFailed()) {
				String ipAddress = member.getAddress();
				int port = member.getPort();
				
				failedMembers.add(new InetSocketAddress(ipAddress, port));
			}
		}
		
		return failedMembers;
	}
	
	public ArrayList<InetSocketAddress> getAllMembers() {
		// used to prevent resizing of ArrayList.
		int initialSize = memberList.size();
		ArrayList<InetSocketAddress> allMembers = new ArrayList<InetSocketAddress>(initialSize);
		
		for (String key : memberList.keySet()) {
			Member member = memberList.get(key);
			
			String ipAddress = member.getAddress();
			int port = member.getPort();
			
			allMembers.add(new InetSocketAddress(ipAddress, port));
		
		}
	
		return allMembers;
	}
	
	
	public void setOnNewMemberHandler(GossipUpdater onNewMember) {
		this.onNewMember = onNewMember;
	}
	
	public void setOnFailedMemberHandler(GossipUpdater onFailedMember) {
		this.onFailedMember = onFailedMember;
	}
	
	public void setOnRevivedMemberHandler(GossipUpdater onRevivedMember) {
		this.onRevivedMember = onRevivedMember;
	}
	
	public void setOnRemoveMemberHandler(GossipUpdater onRemovedMember) {
		this.onRemovedMember = onRemovedMember;
	}
}

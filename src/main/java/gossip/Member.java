package main.java.gossip;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


public class Member implements Serializable {
	private final String IP_ADDRESS;
	private final int PORT;
	
	private long heartbeatSequenceNumber = 0;
	private LocalDateTime lastUpdateTime = null;
	
	private boolean hasFailed = false;
	
	private Config config;
	
	public Member(String ipAddress, int port, long initialHearbeatSequenceNumber, Config config) {
		IP_ADDRESS = ipAddress;
		PORT = port;
		this.config = config;
		
		updateLastUpdateTime();
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	public String getAddress() {
		return IP_ADDRESS;
	}
	
	public InetAddress getInetAddress() {
		try {
			return InetAddress.getByName(IP_ADDRESS);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("You entered a bad IP address: " + IP_ADDRESS);
		System.exit(-1);
		return null;
	}
	
	public InetSocketAddress getSocketAddress() {
		return new InetSocketAddress(IP_ADDRESS, PORT);
	}
	
	public int getPort() {
		return PORT;
	}
	
	public String getUniqueId() {
		return IP_ADDRESS + ":" + PORT;
	}
	
	public long getSequenceNumber() {
		return heartbeatSequenceNumber;
	}
	
	public void updateSequenceNumber(long newSequenceNumber) {
		if (newSequenceNumber > heartbeatSequenceNumber) {
			heartbeatSequenceNumber = newSequenceNumber;
			updateLastUpdateTime();
		}
	}
	
	public void updateLastUpdateTime() {
		lastUpdateTime = LocalDateTime.now();
	}
	
	public void incremenetSequenceNumber() {
		heartbeatSequenceNumber++;
		updateLastUpdateTime();
	}
	
	public void checkIfFailed() {
		LocalDateTime failureTime = lastUpdateTime.plusSeconds(config.MEMBER_FAILURE_TIMEOUT);
		LocalDateTime now = LocalDateTime.now();
		
		hasFailed = now.isAfter(failureTime);
	}
	
	public boolean shouldCleanup() {
		if (hasFailed) {
			LocalDateTime cleanupTime = lastUpdateTime.plusSeconds(config.MEMBER_FAILURE_TIMEOUT + config.MEMBER_CLEANUP_TIMEOUT);
			LocalDateTime now = LocalDateTime.now();
			
			return now.isAfter(cleanupTime);
		} else {
			return false;			
		}
	}
	
	public boolean hasFailed() {
		return hasFailed;
	}
	
	
	
	public String getNetworkMessage() {
		return "[" + IP_ADDRESS + ":" + PORT + "-" + heartbeatSequenceNumber + "]";
	}
	
	
}

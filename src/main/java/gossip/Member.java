package main.java.gossip;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


public class Member implements Serializable {
	private String ipAddress;
	private int port;
	
	private long heartbeatSequenceNumber = 0;
	private LocalDateTime lastUpdateTime = null;
	
	private boolean hasFailed = false;
	
	private Config config;
	
	public Member(String ipAddress, int port, long initialHearbeatSequenceNumber, Config config) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.config = config;
		
		lastUpdateTime = LocalDateTime.now();
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	public String getAddress() {
		return ipAddress;
	}
	
	public InetAddress getInetAddress() {
		try {
			return InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("You entered a bad IP address: " + ipAddress);
		System.exit(-1);
		return null;
	}
	
	public InetSocketAddress getSocketAddress() {
		return new InetSocketAddress(ipAddress, port);
	}
	
	public int getPort() {
		return port;
	}
	
	public String getUniqueId() {
		return ipAddress + ":" + port;
	}
	
	public long getSequenceNumber() {
		return heartbeatSequenceNumber;
	}
	
	public void updateSequenceNumber(long newSequenceNumber) {
		if (newSequenceNumber > heartbeatSequenceNumber) {
			heartbeatSequenceNumber = newSequenceNumber;
			lastUpdateTime = LocalDateTime.now();
		}
	}
	
	public void incremenetSequenceNumber() {
		heartbeatSequenceNumber++;
		lastUpdateTime = LocalDateTime.now();
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
		return "[" + ipAddress + ":" + port + "-" + heartbeatSequenceNumber + "]";
	}
	
	
}

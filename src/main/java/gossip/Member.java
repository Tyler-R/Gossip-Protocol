package main.java.gossip;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


public class Member implements Serializable {
	private final InetSocketAddress address;
	
	private long heartbeatSequenceNumber = 0;
	private LocalDateTime lastUpdateTime = null;
	
	private boolean hasFailed = false;
	
	private Config config;
	
	public Member(InetSocketAddress address, long initialHearbeatSequenceNumber, Config config) {
		this.address = address;
		this.config = config;
		
		updateLastUpdateTime();
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	public String getAddress() {
		return address.getHostName();
	}
	
	public InetAddress getInetAddress() {
		return address.getAddress();
	}
	
	public InetSocketAddress getSocketAddress() {
		return address;
	}
	
	public int getPort() {
		return address.getPort();
	}
	
	public String getUniqueId() {
		return address.toString();
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
		return "[" + address.getHostName() + ":" + address.getPort() + "-" + heartbeatSequenceNumber + "]";
	}
	
	
}

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


public class Member implements Serializable {
	private String ipAddress;
	private int port;
	
	private long heartbeatSequenceNumber = 0;
	private LocalDateTime lastUpdateTime = null;
	
	private boolean hasFailed = false;
	
	private long failureTimeout = 5;
	private long cleanupTimeout = 10;
	
	public Member(String ipAddress, int port, long initialHearbeatSequenceNumber) {
		this.ipAddress = ipAddress;
		this.port = port;
		
		lastUpdateTime = LocalDateTime.now();
	}
	
	public InetAddress getAddress() {
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
			hasFailed = false;
		}
	}
	
	public void incremenetSequenceNumber() {
		heartbeatSequenceNumber++;
		lastUpdateTime = LocalDateTime.now();
	}
	
	public void checkIfFailed() {
		LocalDateTime failureTime = lastUpdateTime.plusSeconds(failureTimeout);
		LocalDateTime now = LocalDateTime.now();
		
		hasFailed = failureTime.isAfter(now);
	}
	
	public boolean shouldCleanup() {
		if (hasFailed) {
			LocalDateTime cleanupTime = lastUpdateTime.plusSeconds(cleanupTimeout);
			LocalDateTime now = LocalDateTime.now();
			
			return cleanupTime.isAfter(now);
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

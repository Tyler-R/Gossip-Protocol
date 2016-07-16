import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.GregorianCalendar;


public class Member {
	private String ipAddress;
	private int port;
	
	private long heartbeatSequenceNumber = 0;
	private Date lastUpdateTime = null;
	
	public Member(String ipAddress, int port, long initialHearbeatSequenceNumber) {
		this.ipAddress = ipAddress;
		this.port = port;
		
		lastUpdateTime = new GregorianCalendar().getTime();
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
			lastUpdateTime = new GregorianCalendar().getTime();
		}
	}
	
	public void incremenetSequenceNumber() {
		heartbeatSequenceNumber++;
		lastUpdateTime = new GregorianCalendar().getTime();
	}
	
	public String getNetworkMessage() {
		return "[" + ipAddress + ":" + port + "-" + heartbeatSequenceNumber + "]";
	}
	
	
}

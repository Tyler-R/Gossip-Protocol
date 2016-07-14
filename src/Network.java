import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class Network {
	
	private DatagramSocket socket;
	
	private byte[] receiveBuffer = new byte[1024];
	private DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length); 
	
	public Network(int portToListenOn) {
		try {
			socket = new DatagramSocket(portToListenOn);
		} catch (SocketException e) {
			e.printStackTrace();
			
			System.out.println("Could not initialize datagram socket. Exiting application");
			System.exit(-1);
		}
	}
	
	public void sendMessage(Member target, String message) {
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), target.getAddress(), target.getPort());
		try {
			socket.send(packet);
		} catch (IOException e) {
			System.out.println("Fatal error trying to send: " + packet);
			e.printStackTrace();
			
			System.exit(-1);
		}
		
		System.out.println("send message '" + message + "' to " + target.getAddress() + ":" + target.getPort());
	}
	
	public String receiveMessage() {
		try {
			socket.receive(receivePacket);
			return new String(receivePacket.getData(), 0, receivePacket.getLength());
		} catch (IOException e) {
			System.out.println("Could not properly receive message");
			e.printStackTrace();
		}
		
		assert false;
		return "";
	}
	
}

package main.java.gossip;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
			Gossip.logger.log("Could not initialize datagram socket because: " + e.getMessage());
		}
	}
	
	public void sendMessage(Member target, String message) {
		sendMessage(target, message.getBytes());
	}
	
	public void sendMessage(Member target, Member message) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		try {
			ObjectOutput oo = new ObjectOutputStream(bStream); 
			oo.writeObject(message);
			oo.close();
		} catch (IOException e) {
			Gossip.logger.log("Could not send " + message.getNetworkMessage() + " to [" + target.getSocketAddress() + "] because: " + e.getMessage());
		}

		byte[] serializedMessage = bStream.toByteArray();
		
		sendMessage(target, serializedMessage);
		
		
	}
	
	private void sendMessage(Member target, byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, target.getInetAddress(), target.getPort());
		try {
			socket.send(packet);
		} catch (IOException e) {
			Gossip.logger.log("Fatal error trying to send: " + packet + " to [" + target.getSocketAddress() + "]");
			e.printStackTrace();
			
			System.exit(-1);
		}
	}
	
	public Member receiveMessage() {
		try {
			socket.receive(receivePacket);
			
			ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
			Member message = null;
			try {
				message = (Member) iStream.readObject();
			} catch (ClassNotFoundException e) {
				Gossip.logger.log("Error casting Gossip network data to Member class because: " + e.getMessage());
			}
			iStream.close();
			
			return message;			
		} catch (IOException e) {
			Gossip.logger.log("Could not properly receive message because: " + e.getMessage());
		}
		return null;
	}
	
}

package test.java.gossip;
import java.net.InetSocketAddress;
import java.time.Duration;

import main.java.gossip.Config;
import main.java.gossip.Gossip;

public class Main {
	public static void main(String[] args) {		
		Config config = new Config(Duration.ofSeconds(2), Duration.ofSeconds(2), Duration.ofMillis(500), Duration.ofMillis(200), 3);
		
		Gossip.setLogger((message) -> {
			System.out.println("Gossip Error: " + message);
		});
		
		Gossip gossip = new Gossip(new InetSocketAddress("127.0.0.1", 8081), new InetSocketAddress("127.0.0.1", 8080), config);
		Gossip g = new Gossip(new InetSocketAddress("127.0.0.1", 8080), new InetSocketAddress("127.0.0.1", 8081), config);
		Gossip gossip2 = new Gossip(new InetSocketAddress("127.0.0.1", 8082), new InetSocketAddress("192.168.0.188", 8081), config);
		Gossip gossip3 = new Gossip(new InetSocketAddress("127.0.0.1", 8083), new InetSocketAddress("192.168.0.188", 8081), config);
		
		gossip.start();
		g.start();
		gossip2.start();
		gossip3.start();
		
		new Thread(()-> {
			try {
				Thread.sleep(1000);
				g.stop();
				gossip2.stop();
				gossip3.stop();
				
				System.out.println("stopped");
				
				Thread.sleep(2500);
				System.out.println("trying");
				
				for (InetSocketAddress addr : gossip.getFailedMembers()) {
					System.out.println(addr.getHostName() + " - " + addr.getPort());
				}
				
				for (InetSocketAddress addr : gossip.getAllMembers()) {
					System.out.println("all: " + addr.getHostName() + " - " + addr.getPort());
				}
				
				Thread.sleep(3500);
				
				for (InetSocketAddress addr : gossip.getAllMembers()) {
					System.out.println("all: " + addr.getHostName() + " - " + addr.getPort());
				}
				
				for (InetSocketAddress addr : gossip.getAliveMembers()) {
					System.out.println("alive: " + addr.getHostName() + " - " + addr.getPort());
				}
				
				for (InetSocketAddress addr : gossip.getFailedMembers()) {
					System.out.println("fail: " + addr.getHostName() + " - " + addr.getPort());
				}
				
				for (InetSocketAddress addr : g.getAliveMembers()) {
					System.out.println(addr.getHostName() + " - " + addr.getPort());
				}
				
				gossip.stop();
				g.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("exiting program");
			
			System.exit(10);
		}).start();;
	}
}

package test.java.gossip;
import main.java.gossip.Config;
import main.java.gossip.Gossip;

public class Main {
	public static void main(String[] args) {
		Config config = new Config(2, 2, 3, 500, 200);
		
		Gossip gossip = new Gossip("127.0.0.1", 8081, "127.0.0.1", 8080, config);
		Gossip g = new Gossip("127.0.0.1", 8080, "127.0.0.1", 8081, config);
//		Gossip gossip2 = new Gossip("127.0.0.1", 8082, "192.168.0.188", 8081);
//		Gossip gossip3 = new Gossip("127.0.0.1", 8083, "192.168.0.188", 8081);
		
		gossip.start();
		g.start();
//		gossip2.start();
//		gossip3.start();
		
		new Thread(()-> {
			try {
				Thread.sleep(5000);
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

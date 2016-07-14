
public class Main {
	public static void main(String[] args) {
		Gossip gossip = new Gossip(8081, "127.0.0.1", 8080);
		Gossip g = new Gossip(8080, "127.0.0.1", 8081);
		
		gossip.start();
		g.start();
	}
}

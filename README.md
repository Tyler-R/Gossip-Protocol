# Gossip-Protocol
An implementation of the Gossip Protocol for failure detection and cluster membership in a distributed system.

## Gossip API

Methods:
```Java
void start()
void stop()
ArrayList<InetSocketAddress> getAliveMembers()
ArrayList<InetSocketAddress> getFailedMembers()
ArrayList<InetSocketAddress> getAllMembers()
```
Event Handlers:
```Java
void setOnNewMemberHandler(GossipUpdater onNewMember)
void setOnFailedMemberHandler(GossipUpdater onFailedMember)
void setOnRevivedMemberHandler(GossipUpdater onRevivedMember)
void setOnRemoveMemberHandler(GossipUpdater onRemovedMember)
````
Class Methods
```Java
static void setLogger(Logger logger)
```


# Example
```Java
public static void main(String[] args) {
    Config config = new Config( Duration.ofSeconds(2), Duration.ofSeconds(2), 
                                Duration.ofMillis(500), Duration.ofMillis(200), 3);
    
    // Set how the error messages will be handled.
    Gossip.setLogger((message) -> {
        System.out.println("Gossip Error: " + message);
    });
  
    Gossip firstNode = new Gossip(new InetSocketAddress("127.0.0.1", 8080), config);
  
    firstNode.setOnNewMemberHandler( (address) -> {
        System.out.println(address + " connected to first node");
    });
  
    firstNode.start();
  
  
    // Create 20 nodes that connect in a chair to each other. Despite only 1 node connecting to the
    // first node, the first node will eventually have a membership list with all the nodes in it.
    for(int i = 1; i <= 20; i++) {
        Gossip g = new Gossip( new InetSocketAddress("127.0.0.1", 8080 + i), 
                               new InetSocketAddress("127.0.0.1", 8080 + i - 1), config);
        g.start();
    }
}

```

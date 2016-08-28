# Gossip-Protocol
An implementation of the Gossip Protocol for failure detection and cluster membership in a distributed system.

# API

## Gossip API

Constructors:
```Java
Gossip(InetSocketAddress listeningAddress, Config config)
Gossip(InetSocketAddress listeningAddress, InetSocketAddress targetAddress, Config config)
```
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

## GossipUpdater API
```Java
void update(InetSocketAddress address);
```

## Config API
```Java
Config(Duration memberFailureTimeout, Duration memberCleanupTimeout, 
       Duration UpdateFrequency, Duration failureDetectionFrequency, 
       int peersToUpdatePerInterval)
```

# Example

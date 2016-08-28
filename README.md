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

# Example

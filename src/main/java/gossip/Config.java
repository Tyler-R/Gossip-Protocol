package main.java.gossip;

import java.io.Serializable;

public class Config implements Serializable {
	
	// Values for members class to use
	public final int MEMBER_FAILURE_TIMEOUT;
	public final int MEMBER_CLEANUP_TIMEOUT;
	
	// Values for Gossip class to use
	public final int PEERS_TO_UPDATE_PER_INTERVAL; 
	public final int UPDATE_FREQUENCY;
	public final int FAILURE_DETECTION_FREQUENCY;
	
	public Config(int memberFailureTimeout, int memberCleanupTimeout, 
			int peersToUpdatePerInterval, int UpdateFrequency, int failureDetectionFrequency) {
		MEMBER_FAILURE_TIMEOUT = memberFailureTimeout;
		MEMBER_CLEANUP_TIMEOUT = memberCleanupTimeout;
		
		PEERS_TO_UPDATE_PER_INTERVAL = peersToUpdatePerInterval;
		UPDATE_FREQUENCY = UpdateFrequency;
		FAILURE_DETECTION_FREQUENCY = failureDetectionFrequency;
		
	}
}

package com.salesforce.bazel.sdk.bep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstraction for a Bazel Build Event Protocol event stream.
 * See docs/buildeventprotocol.md for details. 
 */
public abstract class BazelBuildEventStream {
    
    List<BazelBuildEventSubscriber> subscribeAll = new ArrayList<>();
    Map<String, BazelBuildEventSubscriber> subscribeFiltered = new HashMap<>();

    /**
     * Subscribe to all BEP events.
     */
    public void subscribe(BazelBuildEventSubscriber subscriber) {
        subscribeAll.add(subscriber);
    }

    /**
     * Subscribe to particular BEP events.
     */
    public void subscribe(BazelBuildEventSubscriber subscriber, Set<String> eventTypes) {
        for (String eventType : eventTypes) {
            subscribeFiltered.put(eventType, subscriber);
        }
    }
    
    protected void publishEvent(BazelBuildEvent event) {
        String eventName = null;
    }

}

package com.salesforce.bazel.sdk.bep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.salesforce.bazel.sdk.bep.event.BEPProgressEvent;
import com.salesforce.bazel.sdk.bep.event.BEPStartedEvent;
import com.salesforce.bazel.sdk.bep.event.BEPTargetCompletedEvent;

public class BazelBuildEventTypeManager {
    
    /**
     * List of resolvers that can map a textual BEP event type to a concrete implementation.
     * SDK users can add their own resolver if they need to process more BEP event types than
     * what the SDK supports.
     */
    private static List<BazelBuildEventTypeResolver> resolvers = new ArrayList<>();
    static {
        resolvers.add(new DefaultBazelBuildEventTypeResolver());
    }
    
    /**
     * List of recognized BEP event types. BEP supports many event types that the SDK
     * does not implement because they are uncommon or of limited use. The SDK just 
     * instantiates these event types and ignores any others.
     */
    protected static Set<String> eventTypes = new HashSet<>();
    static {
        eventTypes.add("buildFinished");
        eventTypes.add("buildMetrics");
        eventTypes.add("configuration");
        eventTypes.add("optionsParsed");
        eventTypes.add("pattern");
        eventTypes.add(BEPProgressEvent.NAME);
        eventTypes.add(BEPStartedEvent.NAME);
        eventTypes.add(BEPTargetCompletedEvent.NAME);
        eventTypes.add("testResult");
        eventTypes.add("testSummary");
        eventTypes.add("unstructuredCommand");
    }

    /**
     * Add a new event type to the supported list. Requires that you also register
     * a BazelBuildEventTypeResolver.
     */
    public static void addEventType(String eventType) {
        eventTypes.add(eventType);
    }
    
    /**
     * Add a new BEP event type resolver. 
     */
    public static void registerTypeResolver(BazelBuildEventTypeResolver resolver) {
        // insert the new resolver at the head of the line
        resolvers.add(0, resolver);
    }
    
    /**
     * Normally called by a BazelBuildEventsStream while loading an event json.
     */
    public static BazelBuildEvent parseEvent(String json, int index) {
        BazelBuildEvent event = null;
        
        try {
            JSONObject eventObject = (JSONObject)new JSONParser().parse(json);
            JSONObject id = (JSONObject)eventObject.get("id");
            
            if (id != null) {
                // it is a little awkward to determine the event type, since the type is expressed 
                // as a key name, not a key value. So we iterate through the type we care about and
                // look for a key match.
                for (String eventType : eventTypes) {
                    Object type = id.get(eventType);
                    if (type != null) {
                        // this is a supported type in the SDK
                        event = createEvent(eventType, index, eventObject);
                    }
                }
            }
        } catch (Exception anyE) {
            anyE.printStackTrace();
            return null;
        }
        
        return event;
    }
    
    /**
     * Used by a BazelBuildEventStream to create an event object when it receives the json event. 
     */
    static BazelBuildEvent createEvent(String eventType, int index, JSONObject eventObject) {
        BazelBuildEvent event = null;
        
        for (BazelBuildEventTypeResolver resolver : resolvers) {
            event = resolver.createEvent(eventType, index, eventObject);
            if (event != null) {
                return event;
            }
        }
        return event;
    }
}

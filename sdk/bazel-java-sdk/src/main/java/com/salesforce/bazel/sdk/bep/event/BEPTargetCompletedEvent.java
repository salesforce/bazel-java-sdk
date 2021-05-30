package com.salesforce.bazel.sdk.bep.event;

import org.json.simple.JSONObject;

import com.salesforce.bazel.sdk.bep.BazelBuildEvent;

public class BEPTargetCompletedEvent extends BazelBuildEvent {
    
    public static final String NAME = "targetCompleted";

    public BEPTargetCompletedEvent(int index, JSONObject eventObject) {
        super(NAME, index, eventObject);
    }

}

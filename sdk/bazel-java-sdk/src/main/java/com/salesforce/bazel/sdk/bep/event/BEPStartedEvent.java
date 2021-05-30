package com.salesforce.bazel.sdk.bep.event;

import org.json.simple.JSONObject;

import com.salesforce.bazel.sdk.bep.BazelBuildEvent;

public class BEPStartedEvent extends BazelBuildEvent{
    public static final String NAME = "started";

    public BEPStartedEvent(int index, JSONObject eventObject) {
        super(NAME, index, eventObject);
    }
}

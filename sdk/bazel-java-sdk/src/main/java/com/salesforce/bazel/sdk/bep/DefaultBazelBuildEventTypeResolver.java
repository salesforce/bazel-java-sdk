package com.salesforce.bazel.sdk.bep;

import org.json.simple.JSONObject;

import com.salesforce.bazel.sdk.bep.event.BEPProgressEvent;
import com.salesforce.bazel.sdk.bep.event.BEPStartedEvent;
import com.salesforce.bazel.sdk.bep.event.BEPTargetCompletedEvent;

class DefaultBazelBuildEventTypeResolver implements BazelBuildEventTypeResolver {

    @Override
    public BazelBuildEvent createEvent(String eventType, int index, JSONObject eventObject) {
        BazelBuildEvent event = null;
        switch (eventType) {
        case BEPProgressEvent.NAME:
            event = new BEPProgressEvent(index, eventObject);
            break;
        case BEPStartedEvent.NAME:
            event = new BEPStartedEvent(index, eventObject);
            break;
        case BEPTargetCompletedEvent.NAME:
            event = new BEPTargetCompletedEvent(index, eventObject);
            break;
        }
        return event;
    }
}

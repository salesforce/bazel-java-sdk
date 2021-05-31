package com.salesforce.bazel.sdk.bep;

import org.json.simple.JSONObject;

import com.salesforce.bazel.sdk.bep.event.BEPBuildFinishedEvent;
import com.salesforce.bazel.sdk.bep.event.BEPBuildMetricsEvent;
import com.salesforce.bazel.sdk.bep.event.BEPProgressEvent;
import com.salesforce.bazel.sdk.bep.event.BEPStartedEvent;
import com.salesforce.bazel.sdk.bep.event.BEPTargetCompletedEvent;
import com.salesforce.bazel.sdk.bep.event.BEPTestResultEvent;
import com.salesforce.bazel.sdk.bep.event.BazelBuildEvent;

class DefaultBazelBuildEventTypeResolver implements BazelBuildEventTypeResolver {

    @Override
    public BazelBuildEvent createEvent(String eventType, String rawEvent, int index, JSONObject eventObject) {
        BazelBuildEvent event = null;
        switch (eventType) {
        case BEPProgressEvent.NAME:
            event = new BEPProgressEvent(rawEvent, index, eventObject);
            break;
        case BEPStartedEvent.NAME:
            event = new BEPStartedEvent(rawEvent, index, eventObject);
            break;
        case BEPTargetCompletedEvent.NAME:
            event = new BEPTargetCompletedEvent(rawEvent, index, eventObject);
            break;
        case BEPBuildFinishedEvent.NAME:
            event = new BEPBuildFinishedEvent(rawEvent, index, eventObject);
            break;
        case BEPBuildMetricsEvent.NAME:
            event = new BEPBuildMetricsEvent(rawEvent, index, eventObject);
            break;
        case BEPTestResultEvent.NAME:
            event = new BEPTestResultEvent(rawEvent, index, eventObject);
            break;
        default:
            event = new BazelBuildEvent(eventType, rawEvent, index, eventObject);
        }
        return event;
    }
}

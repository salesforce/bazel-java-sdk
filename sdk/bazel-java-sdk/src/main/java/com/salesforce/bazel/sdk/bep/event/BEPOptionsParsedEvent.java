package com.salesforce.bazel.sdk.bep.event;

import org.json.simple.JSONObject;

/**
 * Model for the Options Parsed BEP event.
 * <p>
 * This event is useful when you want to see the arguments and parameters
 * used by Bazel to launch the build/test operation.
 */
public class BEPOptionsParsedEvent extends BEPEvent {
    public static final String NAME = "optionsParsed";

    public BEPOptionsParsedEvent(String rawEvent, int index, JSONObject eventObj) {
        super(NAME, rawEvent, index, eventObj);
    }

}

package com.salesforce.bazel.sdk.bep.event;

import java.util.List;

import org.json.simple.JSONObject;

import com.salesforce.bazel.sdk.bep.BazelBuildEvent;

public class BEPProgressEvent extends BazelBuildEvent {

    public static final String NAME = "progress";
    protected List<String> stderr;

    public BEPProgressEvent(int index, JSONObject eventObject) {
        super(NAME, index, eventObject);
        
        JSONObject progressDetail = (JSONObject)eventObject.get("progress");
        if (progressDetail != null) {
            Object stderr = progressDetail.get("stderr");
            if (stderr != null) {
                String stderrStr = stderr.toString();
                if (stderrStr.startsWith("ERROR:") || stderrStr.contains("FAILED")) {
                    this.stderr = splitAndCleanAndDedupeLines(stderrStr);
                    this.isError = true;
                }
            }
        }
    }

}

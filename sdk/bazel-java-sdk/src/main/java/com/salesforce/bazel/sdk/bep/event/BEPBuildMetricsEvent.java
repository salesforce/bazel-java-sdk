package com.salesforce.bazel.sdk.bep.event;

import org.json.simple.JSONObject;

/**
 * Model for the Build Metrics BEP event.
 */
public class BEPBuildMetricsEvent extends BEPEvent {
    public static final String NAME = "buildMetrics";

    private String actionsCreated;
    private String actionsExecuted;
    private long usedHeapSizePostBuild = 0L;
    private int cpuTimeInMs = 0;
    private int wallTimeInMs = 0;
    private int analysisPhaseTimeInMs = 0;
    
    
    public BEPBuildMetricsEvent(String rawEvent, int index, JSONObject eventObj) {
        super(NAME, rawEvent, index, eventObj);
        
        JSONObject metricsDetail = (JSONObject)eventObj.get("buildMetrics");
        if (metricsDetail != null) {
            parseDetails(metricsDetail);
        }
        
    }
    
    // GETTERS
    
    public String getActionsCreated() {
        return actionsCreated;
    }

    public String getActionsExecuted() {
        return actionsExecuted;
    }

    public long getUsedHeapSizePostBuild() {
        return usedHeapSizePostBuild;
    }

    public int getCpuTimeInMs() {
        return cpuTimeInMs;
    }

    public int getWallTimeInMs() {
        return wallTimeInMs;
    }

    public int getAnalysisPhaseTimeInMs() {
        return analysisPhaseTimeInMs;
    }

    // PARSER
    
    // Notice the numbers are encoded as strings not numbers
    /*
       "buildMetrics": {
        "actionSummary": { "actionsCreated": "2", "actionsExecuted": "2" },
        "memoryMetrics": { "usedHeapSizePostBuild":"31446304" },
        "timingMetrics": {
          "cpuTimeInMs": "647",
          "wallTimeInMs": "3459",
          "analysisPhaseTimeInMs": "23",
        }
       }
     */

    void parseDetails(JSONObject metricsDetail) {
        JSONObject actionSummaryObj = (JSONObject)metricsDetail.get("actionSummary");
        if (actionSummaryObj != null) {
            actionsCreated = this.decodeStringFromJsonObject(actionSummaryObj.get("actionsCreated"));
            actionsExecuted = this.decodeStringFromJsonObject(actionSummaryObj.get("actionsExecuted"));
        }
        JSONObject memoryObj = (JSONObject)metricsDetail.get("memoryMetrics");
        if (memoryObj != null) {
            Object heapObj = memoryObj.get("usedHeapSizePostBuild");
            usedHeapSizePostBuild = decodeLongFromJsonObject(heapObj);
        }
        JSONObject timingObj = (JSONObject)metricsDetail.get("timingMetrics");
        if (timingObj != null) {
            Object cpuObj = timingObj.get("cpuTimeInMs");
            cpuTimeInMs = decodeIntFromJsonObject(cpuObj);
            Object wallObj = timingObj.get("wallTimeInMs");
            wallTimeInMs = decodeIntFromJsonObject(wallObj);
            Object apObj = timingObj.get("analysisPhaseTimeInMs");
            analysisPhaseTimeInMs = decodeIntFromJsonObject(apObj);
        }
    }

    // TOSTRING
    
    @Override
    public String toString() {
        return "BEPBuildMetricsEvent [actionsCreated=" + actionsCreated + ", actionsExecuted=" + actionsExecuted
                + ", usedHeapSizePostBuild=" + usedHeapSizePostBuild + ", cpuTimeInMs=" + cpuTimeInMs
                + ", wallTimeInMs=" + wallTimeInMs + ", analysisPhaseTimeInMs=" + analysisPhaseTimeInMs + ", index="
                + index + ", eventType=" + eventType + ", isProcessed=" + isProcessed + ", isLastMessage="
                + isLastMessage + ", isError=" + isError + "]";
    }
}

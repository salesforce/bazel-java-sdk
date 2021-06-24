package com.salesforce.bazel.app.subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.salesforce.bazel.sdk.bep.BazelBuildEventSubscriber;
import com.salesforce.bazel.sdk.bep.event.BEPEvent;
import com.salesforce.bazel.sdk.bep.event.BEPFileUri;
import com.salesforce.bazel.sdk.bep.event.BEPStartedEvent;
import com.salesforce.bazel.sdk.bep.event.BEPTestResultEvent;

public class ExampleBazelEventSubscriber implements BazelBuildEventSubscriber {

    private Map<String, Integer> eventCounts = new HashMap<>();
    
    @Override
    public void onEvent(BEPEvent event) {
        System.out.println("Subscriber received event ["+event.getIndex()+"] "+event.toString());
        countEvent(event);
        
        // PROGRESS
//        if (BEPProgressEvent.NAME.equals(event.getEventType())) {
//            BEPProgressEvent progress = (BEPProgressEvent)event;
//            
//            System.out.println(" >> stdout");
//            logLines(progress.getStdout());
//            System.out.println(" >> stederr");
//            logLines(progress.getStderr());
//        }

        // ERRORS
        if (event.isError()) {
            System.out.println(" ERROR:");
            
            // TEST FAILED?
            if (BEPTestResultEvent.NAME.equals(event.getEventType())) {
                BEPTestResultEvent testResult = (BEPTestResultEvent)event;
                Map<String, BEPFileUri> outputs = testResult.getActionOutputs();
                for (String name : outputs.keySet()) {
                    if (name.endsWith(".log")) {
                        BEPFileUri fileUri = outputs.get(name);
                        
                        // get the lines from the log file for the test failure; ignore the test log
                        // prior to the actual reporting of the error, which is a line that starts with
                        // "Failures". Ignore any lines after "Test run finished after 1076 ms"
                        String beginRegex = "Failures.*";
                        String endRegex = "Test run finished.*";
                        boolean ignoreBlankLines = true;
                        List<String> lines = fileUri.loadLines(beginRegex, endRegex, ignoreBlankLines);
                        
                        // print the log lines from the testResult event
                        for (String line : lines) {
                            System.out.println("  > "+line);
                        }
                    }
                }
            }
        }
        
        if (event.isLastMessage()) {
            logCounts();
        }
    }

    private void countEvent(BEPEvent event) {
        String type = event.getEventType();
        
        if (BEPStartedEvent.NAME.equals(type)) {
            eventCounts = new HashMap<>();
        }
        
        Integer count = eventCounts.get(type);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        eventCounts.put(type, count);
    }
    
    private void logCounts() {
        System.out.println("Event counts:");
        for (String type : eventCounts.keySet()) {
            int count = eventCounts.get(type);
            System.out.println("  "+type+": "+count);
        }
    }
    
    @SuppressWarnings("unused")
    private void logLines(List<String> loglines) {
        if (loglines != null) {
            for (String line : loglines) {
                System.out.println("   >> "+line);
            }
        }
    }
}

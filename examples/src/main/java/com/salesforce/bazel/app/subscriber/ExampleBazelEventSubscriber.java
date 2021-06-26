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
        System.out.println("Subscriber received event [" + event.getIndex() + "] " + event.toString());
        countEvent(event);

        // PROGRESS
        // Progress events are very verbose, but they have interesting content. Enable this code
        // if you want to peak inside stdout/stderr of your actions
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

            // TEST FAILED?
            if (BEPTestResultEvent.NAME.equals(event.getEventType())) {
                BEPTestResultEvent testResult = (BEPTestResultEvent) event;
                Map<String, BEPFileUri> outputs = testResult.getActionOutputs();
                for (String name : outputs.keySet()) {
                    // find the plain-text test.log file; there is also sometimes (always?) an xml
                    // version of it if you want something structured to parse
                    if (name.endsWith(".log")) {
                        BEPFileUri fileUri = outputs.get(name);

                        // get the lines from the log file for the test failure; the output is entirely dependent
                        // on the test runner (e.g. JUnit test runner).
                        String beginRegex = "There was.*";
                        String endRegex = ".*shutdown.*";
                        boolean ignoreBlankLines = true;
                        List<String> lines = fileUri.loadLines(beginRegex, endRegex, ignoreBlankLines);

                        // print the log lines from the testResult event
                        for (String line : lines) {
                            System.out.println("  > " + line);
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
            System.out.println("  " + type + ": " + count);
        }
    }

    @SuppressWarnings("unused")
    private void logLines(List<String> loglines) {
        if (loglines != null) {
            for (String line : loglines) {
                System.out.println("   >> " + line);
            }
        }
    }
}

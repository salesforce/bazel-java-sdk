package com.salesforce.bazel.app.subscriber;

import java.util.List;
import java.util.Map;

import com.salesforce.bazel.sdk.bep.BazelBuildEventSubscriber;
import com.salesforce.bazel.sdk.bep.event.BEPFileUri;
import com.salesforce.bazel.sdk.bep.event.BEPTestResultEvent;
import com.salesforce.bazel.sdk.bep.event.BEPEvent;

public class ExampleBazelEventSubscriber implements BazelBuildEventSubscriber {

    @Override
    public void onEvent(BEPEvent event) {
        System.out.println("Subscriber received event ["+event.getIndex()+"] "+event.toString());
        
        if (event.isError()) {
            System.out.println(" ERROR:");
            
            // TEST FAILED?
            if ("testResult".equals(event.getEventType())) {
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
    }

}

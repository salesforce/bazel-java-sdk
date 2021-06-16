package com.salesforce.bazel.app.subscriber;

import java.io.File;

import com.salesforce.bazel.sdk.bep.BazelBuildEventSubscriber;
import com.salesforce.bazel.sdk.bep.BazelBuildEventsFileStream;
import com.salesforce.bazel.sdk.bep.event.BEPProgressEvent;
import com.salesforce.bazel.sdk.init.JvmRuleInit;
import com.salesforce.bazel.sdk.util.BazelPathHelper;

/**
 * This app uses the Bazel Java SDK to monitor builds in a Bazel workspace on your machine. It
 * hooks into build activity using the Bazel Build Events Protocol (BEP), which allows this app to
 * see various build events like build start, build finish, build errors, test errors, etc. This
 * could be used to power customized notifications to the user, or a team of users.
 * <p>
 * Because this app integrates with Bazel using BEP, this app will work with command line
 * builds but also with IDE builds.
 * <p>
 * <b>NOTE:</b>
 * This app requires a configuration change to your Bazel workspace: you must add the following lines to
 * your .bazelrc file to enable BEP:<br/>
 *   build --build_event_json_file bep_build.json<br/>
 *    test --build_event_json_file bep_test.json<br/>
 * <p>
 * The listener for the events simply prints out each event, but this can be easily customized
 * for other use cases. See ExampleBazelEventSubscriber.java
 * <p>
 * Build:<p>
 * bazel build //examples:BazelSubscriberApp_deploy.jar
 * <p>
 * Usage:
 * <ul>
 * <li>Build: bazel build //examples:BazelSubscriberApp_deploy.jar</li>
 * <li>Args: java -jar bazel-bin/examples/BazelSubscriberApp_deploy.jar [path to Bazel workspace dir to analyze]</li>
 * <li>Example: java -jar bazel-bin/examples/BazelSubscriberApp_deploy.jar /home/mbenioff/dev/my-bazel-ws
 * </ul>
 */
public class BazelSubscriberApp {

    public static final String buildBEPFilename = "bep_build.json";
    public static final String testBEPFilename = "bep_test.json";

    private static String bazelWorkspacePath;
    private static File bazelWorkspaceDir;


    public static void main(String[] args) {
        parseArgs(args);

        // Load the rules support, currently only JVM rules (java_library etc) are supported
        JvmRuleInit.initialize();

        BEPProgressEvent.includeStdOutErrInToString(false);

        File buildBEPFile = new File(bazelWorkspaceDir, buildBEPFilename);
        File testBEPFile = new File(bazelWorkspaceDir, testBEPFilename);

        BazelBuildEventsFileStream bepStream = new BazelBuildEventsFileStream();
        bepStream.addFileToMonitor(buildBEPFile, true);
        //bepStream.addFileToMonitor(testBEPFile, true);
        BazelBuildEventSubscriber exampleSubscriber = new ExampleBazelEventSubscriber();

        // subscribe to all events with this form:
        bepStream.subscribe(exampleSubscriber);

        // filter events to receive only the ones you want:
        //Set<String> eventTypes = new HashSet<>();
        //eventTypes.add("testResult");
        //bepStream.subscribe(exampleSubscriber, eventTypes);


        // start polling
        bepStream.activateStream();
    }

    // HELPERS

    private static void parseArgs(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(
                    "Usage: java -jar BazelBuildApp_deploy.jar [Bazel workspace absolute path]");
        }

        bazelWorkspacePath = args[0];
        bazelWorkspaceDir = new File(bazelWorkspacePath);
        bazelWorkspaceDir = BazelPathHelper.getCanonicalFileSafely(bazelWorkspaceDir);

        if (!bazelWorkspaceDir.exists()) {
            throw new IllegalArgumentException(
                    "Bazel workspace directory does not exist. Usage: java -jar BazelBuildApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        if (!bazelWorkspaceDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "Bazel workspace directory does not exist. Usage: java -jar BazelBuildApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
    }
}

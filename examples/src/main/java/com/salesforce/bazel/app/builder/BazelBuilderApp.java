package com.salesforce.bazel.app.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.salesforce.bazel.sdk.command.BazelWorkspaceCommandRunner;
import com.salesforce.bazel.sdk.command.CommandBuilder;
import com.salesforce.bazel.sdk.command.shell.ShellCommandBuilder;
import com.salesforce.bazel.sdk.console.CommandConsoleFactory;
import com.salesforce.bazel.sdk.console.StandardCommandConsoleFactory;
import com.salesforce.bazel.sdk.model.BazelProblem;
import com.salesforce.bazel.sdk.path.BazelPathHelper;

/**
 * This app, as a tool, is not useful. It simply uses the Bazel Java SDK to run a build on a Bazel workspace. In effect,
 * it is the equivalent to 'bazel build //...'.
 * <p>
 * The value in this app is as a starting point for using the SDK to write tools that are actually useful.
 * <p>
 * Build:
 * <p>
 * bazel build //examples:BazelBuilderApp_deploy.jar
 * <p>
 * Usage:
 * <ul>
 * <li>Build: bazel build //examples:BazelBuilderApp_deploy.jar</li>
 * <li>Args: java -jar bazel-bin/examples/BazelBuilderApp_deploy.jar [path to bazel executable] [path to Bazel workspace
 * dir]</li>
 * <li>Example: java -jar bazel-bin/examples/BazelBuilderApp_deploy.jar /usr/local/bin/bazel ../my-bazel-ws
 * </ul>
 */
public class BazelBuilderApp {
    private static String bazelExecutablePath;
    private static File bazelExecutableFile;
    private static String bazelWorkspacePath;
    private static File bazelWorkspaceDir;

    public static void main(String[] args) throws Exception {
        parseArgs(args);

        // set up the command line env
        CommandConsoleFactory consoleFactory = new StandardCommandConsoleFactory();
        CommandBuilder commandBuilder = new ShellCommandBuilder(consoleFactory);
        BazelWorkspaceCommandRunner bazelWorkspaceCmdRunner = new BazelWorkspaceCommandRunner(bazelExecutableFile, null,
                commandBuilder, consoleFactory, bazelWorkspaceDir);

        // build all
        Set<String> targets = new HashSet<>();
        targets.add("//...");
        List<BazelProblem> problems = bazelWorkspaceCmdRunner.runBazelBuild(targets, new ArrayList<String>());

        // print the problems
        printResult(problems);
    }

    // HELPERS

    private static void parseArgs(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: java -jar BazelBuildApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        bazelExecutablePath = args[0];
        bazelExecutableFile = new File(bazelExecutablePath);
        bazelExecutableFile = BazelPathHelper.getCanonicalFileSafely(bazelExecutableFile);

        bazelWorkspacePath = args[1];
        bazelWorkspaceDir = new File(bazelWorkspacePath);
        bazelWorkspaceDir = BazelPathHelper.getCanonicalFileSafely(bazelWorkspaceDir);

        if (!bazelExecutableFile.exists()) {
            throw new IllegalArgumentException(
                    "Bazel executable path does not exist. Usage: java -jar BazelBuildApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        if (!bazelWorkspaceDir.exists()) {
            throw new IllegalArgumentException(
                    "Bazel workspace directory does not exist. Usage: java -jar BazelBuildApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        if (!bazelWorkspaceDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "Bazel workspace directory does not exist. Usage: java -jar BazelBuildApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
    }

    private static void printResult(List<BazelProblem> problems) {
        if (problems.size() == 0) {
            System.out.println("Build successful.");
        } else {
            System.out.println("Problems found:");
            for (BazelProblem problem : problems) {
                System.out.println(
                    problem.getResourcePath() + ":L" + problem.getLineNumber() + ":  " + problem.getDescription());
            }
        }
    }
}

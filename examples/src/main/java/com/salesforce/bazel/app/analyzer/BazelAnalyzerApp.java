/**
 * Copyright (c) 2020, Salesforce.com, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.bazel.app.analyzer;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.salesforce.bazel.sdk.aspect.AspectDependencyGraphBuilder;
import com.salesforce.bazel.sdk.aspect.AspectTargetInfo;
import com.salesforce.bazel.sdk.aspect.AspectTargetInfos;
import com.salesforce.bazel.sdk.aspect.BazelAspectLocation;
import com.salesforce.bazel.sdk.aspect.LocalBazelAspectLocation;
import com.salesforce.bazel.sdk.command.BazelCommandManager;
import com.salesforce.bazel.sdk.command.BazelWorkspaceCommandOptions;
import com.salesforce.bazel.sdk.command.BazelWorkspaceCommandRunner;
import com.salesforce.bazel.sdk.command.CommandBuilder;
import com.salesforce.bazel.sdk.command.shell.ShellCommandBuilder;
import com.salesforce.bazel.sdk.console.CommandConsoleFactory;
import com.salesforce.bazel.sdk.console.StandardCommandConsoleFactory;
import com.salesforce.bazel.sdk.model.BazelDependencyGraph;
import com.salesforce.bazel.sdk.model.BazelLabel;
import com.salesforce.bazel.sdk.model.BazelPackageInfo;
import com.salesforce.bazel.sdk.model.BazelPackageLocation;
import com.salesforce.bazel.sdk.model.BazelWorkspace;
import com.salesforce.bazel.sdk.util.BazelPathHelper;
import com.salesforce.bazel.sdk.workspace.BazelWorkspaceScanner;
import com.salesforce.bazel.sdk.workspace.OperatingEnvironmentDetectionStrategy;
import com.salesforce.bazel.sdk.workspace.ProjectOrderResolver;
import com.salesforce.bazel.sdk.workspace.ProjectOrderResolverImpl;
import com.salesforce.bazel.sdk.workspace.RealOperatingEnvironmentDetectionStrategy;

/**
 * This app, as a tool, is not useful. It simply uses the Bazel Java SDK to read a Bazel workspace, compute the
 * dependency graph, and a few other tasks.
 * <p>
 * The value in this app is as a starting point for using the SDK to write tools that are actually useful.
 * <p>
 * <ul>
 * <li>bazel build //examples:BazelAnalyzerApp_deploy.jar</li>
 * <li>Usage: java -jar bazel-bin/examples/BazelAnalyzerApp_deploy.jar [path to bazel executable] [path to Bazel workspace dir]</li>
 * <li>Example: java -jar bazel-bin/examples/BazelAnalyzerApp_deploy.jar /usr/local/bin/bazel ../my-bazel-ws
 * </ul>
 */
public class BazelAnalyzerApp {
    private static String bazelExecutablePath;
    private static File bazelExecutableFile;
    private static String bazelWorkspacePath;
    private static File bazelWorkspaceDir;

    // update this to your local environment
    private static final String ASPECT_LOCATION = "sdk/bazel-java-sdk/aspect"; // $SLASH_OK sample code

    private static BazelWorkspaceScanner workspaceScanner = new BazelWorkspaceScanner();

    public static void main(String[] args) throws Exception {
        parseArgs(args);

        // load the aspect (the component we use to introspect the Bazel build) on the file system
        File aspectDir = loadAspectDirectory(ASPECT_LOCATION);

        // set up the command line env
        BazelAspectLocation aspectLocation = new LocalBazelAspectLocation(aspectDir);
        CommandConsoleFactory consoleFactory = new StandardCommandConsoleFactory();
        CommandBuilder commandBuilder = new ShellCommandBuilder(consoleFactory);
        BazelWorkspaceCommandRunner bazelWorkspaceCmdRunner = new BazelWorkspaceCommandRunner(bazelExecutableFile,
                aspectLocation, commandBuilder, consoleFactory, bazelWorkspaceDir);

        // create the Bazel workspace SDK objects
        String workspaceName = BazelWorkspaceScanner.getBazelWorkspaceName(bazelWorkspacePath); // TODO use a File arg
        OperatingEnvironmentDetectionStrategy osDetector = new RealOperatingEnvironmentDetectionStrategy();
        BazelWorkspace bazelWorkspace =
                new BazelWorkspace(workspaceName, bazelWorkspaceDir, osDetector, bazelWorkspaceCmdRunner);
        BazelWorkspaceCommandOptions bazelOptions = bazelWorkspace.getBazelWorkspaceCommandOptions();
        printBazelOptions(bazelOptions);

        // scan for Bazel packages and print them out
        BazelPackageInfo rootPackage = workspaceScanner.getPackages(bazelWorkspaceDir);
        printPackageListToStdOut(rootPackage);
        List<BazelPackageLocation> allPackages = rootPackage.gatherChildren();

        // run the Aspects to compute the dependency data
        AspectTargetInfos aspects = new AspectTargetInfos();
        Map<BazelLabel, Set<AspectTargetInfo>> aspectMap =
                bazelWorkspaceCmdRunner.getAspectTargetInfoForPackages(allPackages, "BazelAnalyzerApp");
        for (BazelLabel target : aspectMap.keySet()) {
            Set<AspectTargetInfo> aspectsForTarget = aspectMap.get(target);
            aspects.addAll(aspectsForTarget);
        }

        // use the dependency data to interact with the dependency graph (print root labels)
        BazelDependencyGraph depGraph = AspectDependencyGraphBuilder.build(aspects, false);
        Set<String> rootLabels = depGraph.getRootLabels();
        printRootLabels(rootLabels);

        // put them in the right order for analysis
        ProjectOrderResolver projectOrderResolver = new ProjectOrderResolverImpl();
        Iterable<BazelPackageLocation> orderedPackages = projectOrderResolver.computePackageOrder(rootPackage, aspects);
        printPackageListOrder(orderedPackages);
    }

    // HELPERS

    private static void parseArgs(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java -jar BazelAnalyzerApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        bazelExecutablePath = args[0];
        bazelExecutableFile = new File(bazelExecutablePath);
        bazelExecutableFile = BazelPathHelper.getCanonicalFileSafely(bazelExecutableFile);

        bazelWorkspacePath = args[1];
        bazelWorkspaceDir = new File(bazelWorkspacePath);
        bazelWorkspaceDir = BazelPathHelper.getCanonicalFileSafely(bazelWorkspaceDir);

        if (!bazelExecutableFile.exists()) {
            throw new IllegalArgumentException("Bazel executable path does not exist. Usage: java -jar BazelAnalyzerApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        if (!bazelWorkspaceDir.exists()) {
            throw new IllegalArgumentException("Bazel workspace directory does not exist. Usage: java -jar BazelAnalyzerApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
        if (!bazelWorkspaceDir.isDirectory()) {
            throw new IllegalArgumentException("Bazel workspace directory does not exist. Usage: java -jar BazelAnalyzerApp_deploy.jar [Bazel executable path] [Bazel workspace absolute path]");
        }
    }

    private static File loadAspectDirectory(String aspectPath) {
        File aspectDir = new File(aspectPath);
        aspectDir = BazelPathHelper.getCanonicalFileSafely(aspectDir);

        if (!aspectDir.exists()) {
            throw new IllegalArgumentException("Aspect directory not found. Update the code ASPECT_LOCATION to point to the 'aspect' directory from bazel-java-sdk.");
        }
        if (!aspectDir.isDirectory()) {
          throw new IllegalArgumentException("bzljavasdk_aspect.bzl not found. Update the code ASPECT_LOCATION to point to the 'aspect' directory from bazel-java-sdk.");
        }
        File aspectFile = new File(aspectDir, "bzljavasdk_aspect.bzl");
        if (!aspectFile.exists()) {
            throw new IllegalArgumentException("bzljavasdk_aspect.bzl not found. Update the code ASPECT_LOCATION to point to the 'aspect' directory from bazel-java-sdk.");
        }
        return aspectDir;
    }


    private static void printBazelOptions(BazelWorkspaceCommandOptions bazelOptions) {
        System.out.println("\nBazel configuration options for the workspace:");
        System.out.println(bazelOptions.toString());
    }

    private static void printPackageListToStdOut(BazelPackageInfo rootPackage) {
        System.out.println("\nFound packages eligible for import:");
        printPackage(rootPackage, "  ", "\n");
    }

    private static void printPackage(BazelPackageInfo pkg, String prefix, String suffix) {
        if (pkg.isWorkspaceRoot()) {
            System.out.println("WORKSPACE" + suffix);
        } else {
            System.out.println(prefix + pkg.getBazelPackageNameLastSegment() + suffix);
        }
        for (BazelPackageInfo child : pkg.getChildPackageInfos()) {
            printPackage(child, prefix + "  ", suffix);
        }
    }

    private static void printRootLabels(Set<String> rootLabels) {
        System.out.println("\n\nRoot labels in the dependency tree (nothing depends on them):");
        for (String label : rootLabels) {
            System.out.println("  " + label + "\n");
        }
    }

    private static void printPackageListOrder(Iterable<BazelPackageLocation> postOrderedModules) {
        System.out.println("\n\nPackages in import order:");
        for (BazelPackageLocation loc : postOrderedModules) {
            System.out.println("  " + loc.getBazelPackageName() + "\n");
        }
    }
}

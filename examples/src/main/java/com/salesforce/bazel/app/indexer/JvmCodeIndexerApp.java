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
package com.salesforce.bazel.app.indexer;

import java.io.File;

import com.salesforce.bazel.sdk.index.jvm.JavaSourceCrawler;
import com.salesforce.bazel.sdk.index.jvm.JvmCodeIndex;
import com.salesforce.bazel.sdk.index.jvm.jar.JarIdentiferResolver;
import com.salesforce.bazel.sdk.index.jvm.jar.JavaJarCrawler;
import com.salesforce.bazel.sdk.index.source.SourceFileCrawler;
import com.salesforce.bazel.sdk.path.FSPathHelper;

/**
 * Indexer for building a JVM type index from nested sets of directories. Supports indexing both source files, and
 * compiled classes in jar files. Includes a command line launcher.
 * <p>
 * Build:
 * <p>
 * bazel build //examples:JvmCodeIndexerApp_deploy.jar
 * <p>
 * This class has knowledge of internal details on how different build systems lay out files.
 * <p>
 * Usage:<br>
 * java -jar JvmCodeIndexerApp_deploy.jar [location of external jar files] [optional: root of directory with source
 * files]
 * <p>
 * <b>USE CASE 1: Bazel Workspace</b>
 * <p>
 * Bazel workspace location on file system: /home/mbenioff/dev/myrepo
 * <p>
 * java -jar JvmCodeIndexerApp_deploy.jar /home/mbenioff/dev/myrepo/bazel-bin/external /home/mbenioff/dev/myrepo
 * <p>
 *
 * <b>USE CASE 2: Maven repository</b>
 * <p>
 * Maven repository location on file system: /home/mbenioff/.m2/repository
 * <p>
 * java -jar examples.jar com.salesforce.bazel.app.indexer.JvmCodeIndexerApp /home/mbenioff/.m2/repository
 */
public class JvmCodeIndexerApp {
    protected String sourceRoot;
    protected String externalJarRoot;

    // COMMAND LINE LAUNCHER
    // This is the best way to learn how to use the JVM indexer.

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(
                "Usage: CodeIndexer [full path to external java jars directory] [optional full path to source root directory]");
            return;
        }

        // build the indexer
        String externalJarRoot = args[0];
        String sourceRoot = null;
        if (args.length > 1) {
            sourceRoot = args[1];
        }
        JvmCodeIndexerApp indexer = new JvmCodeIndexerApp(externalJarRoot, sourceRoot);

        // run the index
        long startTime = System.currentTimeMillis();
        JvmCodeIndex index = indexer.buildIndex();
        long endTime = System.currentTimeMillis();

        // print the results
        index.printIndex();
        System.out.println("\nTotal processing time (milliseconds): " + (endTime - startTime));

    }

    // INDEXER

    public JvmCodeIndexerApp(String externalJarRoot, String sourceRoot) {
        this.sourceRoot = sourceRoot;
        this.externalJarRoot = externalJarRoot;
    }

    public JvmCodeIndex buildIndex() {
        JvmCodeIndex index = new JvmCodeIndex();

        if (externalJarRoot.contains("bazel-out")) {
            if (externalJarRoot.endsWith("bin")) {
                externalJarRoot = FSPathHelper.osSeps(externalJarRoot + FSPathHelper.UNIX_SLASH + "external");
            }
        }
        File externalJarRootFile = new File(externalJarRoot);
        if (!externalJarRootFile.exists()) {
            logError("The provided external java jars directory does not exist. This is invalid.");
            return index;
        }

        JarIdentiferResolver jarResolver = pickJavaJarResolver(externalJarRoot);
        if (jarResolver != null) {
            JavaJarCrawler jarCrawler = new JavaJarCrawler(index, jarResolver);
            jarCrawler.index(externalJarRootFile, true);
        } else {
            logInfo("Could not determine the build system (maven/bazel) from the jar root. Skipping jar scanning...");
        }

        if (sourceRoot != null) {
            File sourceRootFile = new File(sourceRoot);
            if (!sourceRootFile.exists()) {
                logInfo("The provided source code root directory does not exist. This is ok.");
                return index;
            }
            String sourceArtifactMarker = pickJavaSourceArtifactMarker(externalJarRoot);
            logInfo("Looking for source file packages by looking for files named " + sourceArtifactMarker);

            SourceFileCrawler sourceCrawler = new JavaSourceCrawler(index, sourceArtifactMarker);
            sourceCrawler.index(sourceRootFile);
        } else {
            logInfo("The provided source code root directory does not exist. This is ok.");
        }

        return index;
    }

    private static JarIdentiferResolver pickJavaJarResolver(String jarRepoPath) {
        return new JarIdentiferResolver();
    }

    /**
     * What file marks the root of a package?
     */
    private static String pickJavaSourceArtifactMarker(String jarRepoPath) {
        if (jarRepoPath.contains(FSPathHelper.osSeps(".m2/repository"))) { // $SLASH_OK
            return "pom.xml";
        } else if (jarRepoPath.contains("bazel-out") || jarRepoPath.contains("bazel-bin")) {
            return "BUILD"; // TODO BUILD.bazel should be supported too
        }
        // TODO gradle

        return null;
    }

    // override class to redirect this to a real logger
    protected void logInfo(String msg) {
        System.out.println(msg);
    }

    // override class to redirect this to a real logger
    protected void logError(String msg) {
        System.err.println(msg);
    }
}

## Bazel Java SDK

This library is used to programmatically invoke Bazel builds and perform other Bazel operations.
It is implemented in Java and has a clean API and models for working with Bazel.

These are some use cases for *bazel_java_sdk*:
- Code editor and IDE integrations
- Advanced build use cases in which the Bazel command line is not sufficient
- Integrations with other developer ecosystem tools, like Slack and CI systems

:octocat: Please do us a huge favor. If you think this project could be useful for you, now or in the future, please hit the **Star** button at the top.
That helps us advocate for more resources on this project. Thanks!

### Features

- Execution of Bazel commands such as Bazel build and Bazel query and interpretation of the results
- Modeling of Bazel concepts (targets, labels, BUILD files, aspects, etc) and the dependency graph
- JVM Specific Features
  - Creation of a unified class index for Java dependencies and Java targets (e.g. for an IDE find-class feature)
  - Computation of the Java classpath for a Bazel Java target

:lemon: Currently the *bazel_java_sdk* is largely focused on workspaces with Java targets
  (```java_library```, ```java_test```, etc). It can execute commands on any workspace, but
    for the richer dependency analysis features only Java targets will work. Over time we wish
  to add broader support for [other target types](https://github.com/salesforce/bazel-java-sdk/blob/master/sdk/bazel-java-sdk/src/main/java/com/salesforce/bazel/sdk/model/BazelTargetKind.java).

The *bazel_java_sdk* is tested and supported on Mac OS, Linux, and Windows.

### Getting Started

Invoking Bazel commands is the easiest way to get started with the Bazel Java SDK:

```java
import com.salesforce.bazel.sdk.command.BazelCommandManager;
import com.salesforce.bazel.sdk.command.BazelWorkspaceCommandRunner;
import com.salesforce.bazel.sdk.command.CommandBuilder;
import com.salesforce.bazel.sdk.command.shell.ShellCommandBuilder;
import com.salesforce.bazel.sdk.console.CommandConsoleFactory;
import com.salesforce.bazel.sdk.console.StandardCommandConsoleFactory;
import com.salesforce.bazel.sdk.model.BazelProblem;
import com.salesforce.bazel.sdk.util.BazelPathHelper;

...

// set up the Bazel command line environment using the SDK
CommandConsoleFactory consoleFactory = new StandardCommandConsoleFactory();
CommandBuilder commandBuilder = new ShellCommandBuilder(consoleFactory);
BazelWorkspaceCommandRunner bazelWorkspaceCmdRunner = new BazelWorkspaceCommandRunner(
        bazelExecutableFile, null, commandBuilder, consoleFactory, bazelWorkspaceDir);

// Invoke the build all command
Set<String> targets = new HashSet<>();
targets.add("//...");
List<BazelProblem> problems = bazelWorkspaceCmdRunner.runBazelBuild(targets,
   new ArrayList<String>());
```

Assuming you are using Bazel to build your tool (of course!), download the latest release from
 the [Release](https://github.com/salesforce/bazel-java-sdk/releases) list, and add the jar to your project like so:

```starlark
java_import(
    name = "bazel-java-sdk",
    jars = [
        "bazel-java-sdk-1.0.0.jar",
    ],
)
```

### Examples

A great way to be introduced to  *bazel_java_sdk* is through some examples:

[BazelBuilderApp](examples/src/main/java/com/salesforce/bazel/app/builder/BazelBuilderApp.java)
This sample app uses the Bazel Java SDK to run a build on a Bazel workspace. It is the
  equivalent to the command line command ```bazel build //...```.

[BazelAnalyzerApp](examples/src/main/java/com/salesforce/bazel/app/analyzer/BazelAnalyzerApp.java)
This sample app uses the Bazel Java SDK to load a Bazel workspace, compute the
  dependency graph, and a few other tasks.

[Bazel Eclipse Feature](https://github.com/salesforce/bazel-eclipse)
To see a robust implementation, look at the code for the original use case for the  *bazel_java_sdk*.
The Bazel Eclipse Feature is the Eclipse IDE integration with Bazel, and uses the SDK for the
  underlying execution of builds and analsis of classpath.

### Project Management

We use GitHub features to manage the project and to communicate with the community:

- [Issues](https://github.com/salesforce/bazel-java-sdk/issues) please ask questions, report problems, and request new features here
- [Projects](https://github.com/salesforce/bazel-java-sdk/projects) we manage our backlog prioritization here
- [Pull Requests](https://github.com/salesforce/bazel-java-sdk/pulls) want to contribute a fix or a feature? Send us a PR! See our [Contributors Guide](CONTRIBUTING.md).
- [Releases](https://github.com/salesforce/bazel-java-sdk/releases) released versions of the SDK are published here

### Design Tenets

#### Model the Domain

Inside this SDK you will find models for the major concepts of Bazel.
It is our intent to do the tedious but important job of modeling the Bazel build system in Java, such that
  you can leverage the SDK for whatever custom build task is required.
Our goal is to enable you to build a powerful and effective build tool in less than 100 lines of Java code.

#### Minimal Dependencies

Because this SDK will be consumed by larger projects, we do not want to bring any unnecessary baggage into
  the dependency tree.
You will not find libraries such Guava or Spring used by this SDK for this reason.

#### Approachable Coding Style

Our industry struggles to bring in new contributors that do not have formal training in software.
The bar is high to land an initial job in the software engineering profession.
There are boot camps and online courses that are helping to make our industry more approachable, but
  there are other ways we can help.

Build engineering is a great starting role for a (perhaps junior) contributor growing into software engineering.
Maybe this person starts at the help desk, learns some sys admin skills, is then asked to help with the CI system,
  and then becomes the build engineer.
This is great; we want more of this.
Perhaps writing a Bazel build tool using our SDK will be someone's first professional coding project.

For this reason, this SDK is built with all Java skill levels in mind.
We strive to steer clear of the more advanced features of Java that are difficult for learners:
  generics (outside of Collections), streaming API, lambdas, etc.
We also strive to make use of local variables in our code such that each statement performs a single
  operation to make line by line debugging easy to follow.

If you are a learner and encounter areas of difficulty in the SDK, please let us know.
We would be delighted to support you.

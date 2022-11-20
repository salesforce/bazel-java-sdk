## Bazel Java SDK ![BEF Developer Logo](docs/bef_developers_small.png)

This library is used to programmatically invoke Bazel builds and perform other Bazel operations.
It is implemented in Java and has a clean API and models for working with Bazel.

These are some use cases for *bazel-java-sdk*:
- Code editor and IDE integrations
- Advanced build use cases in which the Bazel command line is not sufficient
- Integrations with other developer ecosystem tools, like Slack and CI systems

:octocat: Please do us a huge favor. If you think this project could be useful for you, now or in the future, please hit the **Star** button at the top.
That helps us advocate for more resources on this project. Thanks!

### Features

- Features for any workspace:
  - Execution of Bazel commands such as Bazel build and Bazel query and interpretation of the results
  - Modeling of Bazel concepts (targets, labels, BUILD files, aspects, etc)
  - Parsing the stream of events from [Bazel's Build Event Protocol](docs/buildeventprotocol.md) into event model objects
- Features for workspaces with Java rules:
  - Efficient computation of the dependency graph
  - Generation of the Java classpath for a Bazel Java target
  - Creation of a unified class index for Java dependencies and Java targets (e.g. for an IDE find-any-class feature)

:lemon: Currently the *bazel-java-sdk* is largely focused on workspaces with Java targets
  (```java_library```, ```java_test```, etc). It can execute commands on any workspace, but
    for the richer dependency analysis features only Java targets will work. Over time we wish
  to add broader support for [other target types](https://github.com/salesforce/bazel-java-sdk/blob/master/sdk/bazel-java-sdk/src/main/java/com/salesforce/bazel/sdk/model/BazelTargetKind.java). In addition Java packages must follow
  certain [file layout conventions](conforming_java_packages.md).

The *bazel-java-sdk* is tested and supported on Mac OS, Linux, and Windows.

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

The *bazel-java-sdk* does not need any dependencies.

### Examples

A great way to be introduced to  *bazel-java-sdk* is through some examples:

[BazelBuilderApp](examples/src/main/java/com/salesforce/bazel/app/builder/BazelBuilderApp.java)
This sample app uses the Bazel Java SDK to run a build on a Bazel workspace. It is the
  equivalent to the command line command ```bazel build //...```.
It returns the lines of stdout and stderr to the caller.

[BazelSubscriberApp](examples/src/main/java/com/salesforce/bazel/app/subscriber/BazelSubscriberApp.java)
This sample app uses the Bazel Java SDK to listen for build events emitted by Bazel. This shows the
  SDK support for the Bazel Build Event Protocol (BEP).
For situations in which you need to track what happens in a build, BEP is a great option.
Read about the BEP payloads in [our BEP events document](docs/buildeventprotocol.md).

[BazelJavaAnalyzerApp](examples/src/main/java/com/salesforce/bazel/app/analyzer/BazelAnalyzerApp.java)
This sample app uses the Bazel Java SDK to load a Bazel workspace, compute the
  dependency graph of Java targets, and a few other tasks.

[JvmCodeIndexerApp](examples/src/main/java/com/salesforce/bazel/app/indexer/JvmCodeIndexerApp.java)
This sample app uses the Bazel Java SDK to scan a Bazel workspace, or a Maven .m2 directory, looking
  for jar files and source files to index. This feature is useful for tools such as IDEs that need
  to provide a listing of JVM types available within the Bazel workspace.

[Bazel Eclipse Feature](https://github.com/salesforce/bazel-eclipse)
To see a robust implementation, look at the code for the original use case for the  *bazel-java-sdk*.
The Bazel Eclipse Feature is the Eclipse IDE integration with Bazel, and uses the SDK for the
  underlying execution of builds and analysis of classpath.

### Project Management

We use GitHub features to manage the project and to communicate with the community:

- [Issues](https://github.com/salesforce/bazel-java-sdk/issues) please ask questions, report problems, and request new features here
- [Projects](https://github.com/salesforce/bazel-java-sdk/projects) we manage our backlog prioritization here
- [Pull Requests](https://github.com/salesforce/bazel-java-sdk/pulls) want to contribute a fix or a feature? Send us a PR! See our [Contributors Guide](CONTRIBUTING.md).
- [Releases](https://github.com/salesforce/bazel-java-sdk/releases) released versions of the SDK are published here

🔥 As of now, the major consumer of the SDK is [bazel-eclipse](https://github.com/salesforce/bazel-eclipse). The ongoing development of the SDK is mostly occuring as side effect of [Issues being worked on](https://github.com/salesforce/bazel-eclipse/issues) in that repository. bazel-eclipse has a copy of the SDK, and we are using copy scripts to move code back and forth between Git repositories. 

### Design Tenets

#### Model the Domain

Inside this SDK you will find models for the major concepts of Bazel.
It is our intent to do the tedious but important job of modeling the Bazel build system in Java, such that
  you can leverage the SDK for whatever custom Bazel task is required.
Our goal is to enable you to build a powerful and effective build tool in less than 100 lines of Java code.

#### No Dependencies

Because this SDK will be consumed by larger projects, we do not want to bring any unnecessary baggage into
  the dependency tree.
You will not find libraries such Guava or Spring used by this SDK for this reason.

#### JDK Runtime Support is Lagging

Build engineering is often underfunded within companies.
Toolsets, once built, are commonly unfunded unless a major problem or feature is needed.
That means forcing users to upgrade the JDK to adopt a new version of the SDK is not friendly.
For that reason, the SDK will significantly lag in allowing use of newer JDK features.
That new JDK feature released last month...yeah we won't be using it for years.
For example, as of October 2022 we are still compatible with a JDK8 runtime.

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

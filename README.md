## Bazel Java SDK

This library is used to programmatically invoke Bazel builds and perform other Bazel operations.
It is implemented in Java and has a clean API and models for working with Bazel.

These are some use cases for *bazel_java_sdk*:
- Code editor and IDE integrations
- Advanced build use cases in which the Bazel command line is not sufficient
- Integrations with other developer ecosystem tools, like Slack and CI systems

### Features

- Execution of Bazel commands such as Bazel build and Bazel query and interpretation of the results
- Modeling of Bazel concepts (targets, labels, BUILD files, aspects, etc) and the dependency graph
- Java bells and whistles
  - Creation of a unified class index for Java dependencies and Java targets (e.g. for an IDE find-class feature)
  - Computation of the Java classpath for a Bazel Java target

:lemon: Currently the *bazel_java_sdk* is largely focused on workspaces with Java targets
  (```java_library```, ```java_test```, ```springboot``` etc).

### Examples

The best way to be introduced to  *bazel_java_sdk* is through some examples:

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

### Design Tenets

#### Model the Domain

Inside this SDK you will find models for the major concepts of Bazel.
It is our intent to do the tedious but important of modeling the Bazel build system in Java, such that
  you can leverage the SDK

#### Minimal Dependencies

Because this SDK will be consumed by larger projects, we do not want to bring any unnecessary baggage into
  the dependency tree.
You will not find libraries such Guava or Spring used by this SDK for this reason.

#### Approachable Coding Style

Our industry struggles to bring in new contributors that do not have formal training in software.
The bar is high to land an initial job in the software engineering profession.
There are boot camps and online courses that are helping to make our industry more approachable, but
  there are other ways we can help.

Build engineering is a great transition role for a (perhaps junior) contributor growing into software engineering.
Maybe this person starts at the help desk, learns some sys admin skills, is then asked to help with the CI system,
  and then becomes the build engineer.
This is great, we want more of this.
Perhaps building a Bazel build tool using our SDK will be someone's first coding project.

For this reason, this SDK is built with all Java skill levels in mind.
We strive to steer clear of the more advanced features of Java that are difficult for learners:
  generics (outside of Collections), streaming API, lambdas, etc.
We strive to make use of temporary variables in our code such that each statement performs a single operation
  to make line by line debugging easy to follow.

If you are a learner and encounter areas of difficulty in the SDK, please let us know.
We would be delighted to support you.

## Bazel Java SDK

This library is used to programmatically invoke Bazel builds and perform other Bazel operations.
It is implemented in Java and has a clean API and models for working with Bazel.

These are some use cases for *bazel_java_sdk*:
- Code editor and IDE integrations
- Advanced build use cases in which the Bazel command line is not sufficient
- Integrations with other developer ecosystem tools, like Slack and CI systems

### Features

- Execution of Bazel commands such as Bazel build and Bazel query and interpretation of the results
- Modeling of BUILD files (targets, labels) and the dependency graph
- Creation of a unified class index for Java dependencies and Java targets (e.g. for an IDE find-class feature)
- Computation of the Java classpath for a Bazel Java target

:lemon: Currently the *bazel_java_sdk* dependency features only work against Java targets
  (```java_library``` etc).

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

## Bazel Java SDK Examples

A great way to be introduced to  *bazel-java-sdk* is through some examples:

[BazelBuilderApp](src/main/java/com/salesforce/bazel/app/builder/BazelBuilderApp.java)
This sample app uses the Bazel Java SDK to run a build on a Bazel workspace. It is the
  equivalent to the command line command ```bazel build //...```.
It returns the lines of stdout and stderr to the caller.

[BazelSubscriberApp](src/main/java/com/salesforce/bazel/app/subscriber/BazelSubscriberApp.java)
This sample app uses the Bazel Java SDK to listen for build events emitted by Bazel. This shows the
  SDK support for the Bazel Build Event Protocol (BEP).
For situations in which you need to track what happens in a build, BEP is a great option.
Read about the BEP payloads in [our BEP events document](docs/buildeventprotocol.md).

[BazelJavaAnalyzerApp](src/main/java/com/salesforce/bazel/app/analyzer/BazelAnalyzerApp.java)
This sample app uses the Bazel Java SDK to load a Bazel workspace, compute the
  dependency graph of Java targets, and a few other tasks.

[JvmCodeIndexerApp](src/main/java/com/salesforce/bazel/app/indexer/JvmCodeIndexerApp.java)
This sample app uses the Bazel Java SDK to scan a Bazel workspace, or a Maven .m2 directory, looking
  for jar files and source files to index. This feature is useful for tools such as IDEs that need
  to provide a listing of JVM types available within the Bazel workspace.

[Bazel Eclipse Feature](https://github.com/salesforce/bazel-eclipse)
To see a robust implementation, look at the code for the original use case for the  *bazel-java-sdk*.
The Bazel Eclipse Feature is the Eclipse IDE integration with Bazel, and uses the SDK for the
  underlying execution of builds and analysis of classpath.

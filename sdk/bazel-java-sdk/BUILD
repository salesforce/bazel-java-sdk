java_library(
    name = "bazel-java-sdk",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
    ],
)


java_test(
    name = "ArgumentSplitterTest",
    srcs = [
        "src/main/java/com/salesforce/bazel/sdk/command/ArgumentSplitter.java",
        "src/test/java/com/salesforce/bazel/sdk/command/ArgumentSplitterTest.java",
    ],
)

java_test(
    name = "AspectTargetInfosTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/aspect/AspectTargetInfosTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPBuildFinishedEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPBuildFinishedEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPBuildMetricsEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPBuildMetricsEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPBuildConfigurationEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPBuildConfigurationEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPOptionsParsedEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPOptionsParsedEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPPatternEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPPatternEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPProgressEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPProgressEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPStartedEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPStartedEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPTargetCompletedEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPTargetCompletedEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPTargetConfiguredEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPTargetConfiguredEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPTestResultEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPTestResultEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPTestSummaryEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPTestSummaryEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)

java_test(
    name = "BEPUnstructuredCommandLineEventTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/bep/event/BEPUnstructuredCommandLineEventTest.java",],
    deps = [
        ":bazel-java-sdk",
    ],
)


java_test(
    name = "BazelLabelTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/model/BazelLabelTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelLabelUtilTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/model/BazelLabelUtilTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelProblemTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/model/BazelProblemTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelOutputDirectoryBuilderTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/command/BazelOutputDirectoryBuilderTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelOutputParserTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/command/BazelOutputParserTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelProjectInfoTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/model/BazelProjectInfoTest.java"],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
        "@maven//:org_mockito_mockito_core",
    ],
)

java_test(
    name = "BuildFileSupportTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/workspace/BuildFileSupportTest.java"],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelPackageInfoTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/model/BazelPackageInfoTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "LogHelperTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/logging/CaptureLoggerFacade.java",
        "src/test/java/com/salesforce/bazel/sdk/logging/LogHelperTest.java"],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "ProjectViewTest",
    srcs = [
        "src/test/java/com/salesforce/bazel/sdk/project/ProjectViewTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelWorkspaceTest",
    srcs = glob([
        "src/test/java/com/salesforce/bazel/sdk/model/**/*.java",
    ]),
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-command-mocks",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
        "@maven//:org_mockito_mockito_core",
    ],
)

java_test(
    name = "BazelCommandArgsTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/command/BazelCommandArgsTest.java"],
    deps = [
        ":bazel-java-sdk",

        "@maven//:junit_junit",
    ],
)



java_test(
    name = "BazelLauncherBuilderTest",
    srcs = [
       "src/test/java/com/salesforce/bazel/sdk/command/BazelLauncherBuilderTest.java",
    ],
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-command-mocks",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",

        "@maven//:net_bytebuddy_byte_buddy",
        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
        "@maven//:org_objenesis_objenesis",
    ],
)

java_test(
    name = "BazelWorkspaceScannerTest",
    srcs = ["src/test/java/com/salesforce/bazel/sdk/workspace/BazelWorkspaceScannerTest.java"],
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",
    ],
)


java_test(
    name = "BazelWorkspaceCommandRunnerTest",
    srcs = [
       "src/test/java/com/salesforce/bazel/sdk/command/BazelWorkspaceCommandRunnerTest.java",
    ],
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-command-mocks",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",

        "@maven//:net_bytebuddy_byte_buddy",
        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
        "@maven//:org_objenesis_objenesis",
    ],
)

java_test(
    name = "ShellCommandTest",
    srcs = [
       "src/test/java/com/salesforce/bazel/sdk/command/ShellCommandTest.java",
    ],
    deps = [
        ":bazel-java-sdk",

        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "BazelWorkspaceAspectHelperTest",
    srcs = [
       "src/test/java/com/salesforce/bazel/sdk/command/internal/BazelWorkspaceAspectHelperTest.java",
    ],
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-command-mocks",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",

        "@maven//:net_bytebuddy_byte_buddy",
        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
        "@maven//:org_objenesis_objenesis",
    ],
)

java_test(
    name = "BazelCommandExecutorTest",
    srcs = [
       "src/test/java/com/salesforce/bazel/sdk/command/internal/BazelCommandExecutorTest.java",
    ],
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-command-mocks",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",

        "@maven//:net_bytebuddy_byte_buddy",
        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:junit_junit",
        "@maven//:org_objenesis_objenesis",
    ],
)

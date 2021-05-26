
java_library(
    name = "test-command-mocks",
    srcs = glob([
        "src/main/java/com/salesforce/bazel/sdk/command/test/*.java",
        "src/main/java/com/salesforce/bazel/sdk/command/test/type/*.java",
        "src/main/java/com/salesforce/bazel/sdk/model/test/*.java",
    ]),
    visibility = ["//visibility:public"],
    deps = [
        "//sdk/bazel-java-sdk",
        ":test-workspace-support",

        "@maven//:junit_junit",
        "@maven//:org_mockito_mockito_core",
    ],
)

# Test workspace creator
java_library(
    name = "test-workspace-support",
    srcs = glob(["src/main/java/com/salesforce/bazel/sdk/workspace/test/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
      "//sdk/bazel-java-sdk",
    ],
)

#
# bazel-java-sdk build
#

# Library

java_library(
    name = "bazel-java-sdk",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
       # DO NOT ADD ANY EXTERNAL DEPENDENCY!
       # The SDK has a design tenet to not depend on any external library.
       # The SDK is intended to be embedded in other projects, and having external
       # dependencies complicates that.
    ],
)

# Tests

[java_test(
    name = name[:-len(".java")],
    size = "small",
    srcs = [
      # test
      name,

      # utils
      "src/test/java/com/salesforce/bazel/sdk/logging/CaptureLoggerFacade.java"
    ],
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":bazel-java-sdk",
        "//sdk/bazel-java-sdk-test-framework:test-command-mocks",
        "//sdk/bazel-java-sdk-test-framework:test-workspace-support",

        "@maven//:junit_junit",
        "@maven//:net_bytebuddy_byte_buddy",
        "@maven//:org_hamcrest_hamcrest_core",
        "@maven//:org_mockito_mockito_core",
        "@maven//:org_objenesis_objenesis",
    ],
) for name in glob([
    "src/test/java/**/*Test.java",
])]

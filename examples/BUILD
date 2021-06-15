# Build for the example apps

java_binary(
    name = "BazelAnalyzerApp",
    main_class = "com.salesforce.bazel.app.analyzer.BazelAnalyzerApp",
    srcs = [
      "src/main/java/com/salesforce/bazel/app/analyzer/BazelAnalyzerApp.java",
    ],
    deps = [
      "//sdk/bazel-java-sdk",
    ]
)

java_binary(
    name = "BazelBuilderApp",
    main_class = "com.salesforce.bazel.app.builder.BazelBuilderApp",
    srcs = [
      "src/main/java/com/salesforce/bazel/app/builder/BazelBuilderApp.java",
    ],
    deps = [
      "//sdk/bazel-java-sdk",
    ]
)

java_binary(
    name = "JvmCodeIndexerApp",
    main_class = "com.salesforce.bazel.app.indexer.JvmCodeIndexerApp",
    srcs = [
      "src/main/java/com/salesforce/bazel/app/indexer/JvmCodeIndexerApp.java",
    ],
    deps = [
      "//sdk/bazel-java-sdk",
    ]
)

java_binary(
    name = "BazelSubscriberApp",
    main_class = "com.salesforce.bazel.app.subscriber.BazelSubscriberApp",
    srcs = [
      "src/main/java/com/salesforce/bazel/app/subscriber/BazelSubscriberApp.java",
      "src/main/java/com/salesforce/bazel/app/subscriber/ExampleBazelEventSubscriber.java",
    ],
    deps = [
      "//sdk/bazel-java-sdk",
    ]
)

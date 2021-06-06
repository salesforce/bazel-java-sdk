# Bazel Build Event Protocol Support

The [Build Event Protocol](https://docs.bazel.build/versions/master/build-event-protocol.html) is a
  way for tools to tap into the internal event stream of a build.
Please consult the BEP documentation linked above for a full explanation.

If you want to use the full model of BEP in Java, you can generate the Java bindings of the
[build_event_stream.proto](https://github.com/bazelbuild/bazel/blob/master/src/main/java/com/google/devtools/build/lib/buildeventstream/proto/build_event_stream.proto)
file using _protobuf_.
This would provide access to the full set of event types and fields.
But it is our opinion that very few tools need that - there are many low level events that
  are unnecessary for most tools.

Therefore we chose to provide a simpler set of Java bindings of BEP events in the _bazel_java_sdk_.
It is the small subset of BEP events that are generally useful.
We believe this will be sufficient for most tooling use cases.

If you need support for an additional event type, you can easily extend the included set
  (please consider contributing that back, thanks!).
And again, you can always directly use the .proto file using _protobuf_ if that is better for you.

## Tapping into the BEP Event Stream

There are two main approaches for doing this in Bazel:
- Implement a gRPC server in your tool that will receive BEP events from the Bazel process. See the BEP documentation for [details of the binary approach](https://docs.bazel.build/versions/master/build-event-protocol.html#consume-in-binary-format).
- Configure Bazel to serialize the events to a json file, which can be monitored and parsed by your tool.

The _bazel_java_sdk_ follows the second approach for reasons of simplicity.

### Bazel BEP Json File

To enable this feature, you will need to add this to your build's .bazelrc file:

```
build --build_event_json_file bep_build.json
test --build_event_json_file bep_test.json
```

Bazel truncates the json file when the build/test starts, and then
 the events are appended to the file as they are emitted by Bazel.
When the last event is emitted from the build/test operation, a property
  _lastMessage_ is set on the event to signal that the operation is complete.

The basic flow that is typical for a tool:
- record the last modified time of the BEP json file
- set a timer to awake every 5-10 seconds
- when the last modified time changes:
  - parse the file, looking for the _lastMessage_ property
  - if not found, sleep
  - if found, analyze the events in the file


## Event Type Catalog

The events listed below are recognized by the SDK.
There are at least 23 BEP event types, but many are not relevant for most tools.
These are the curated list that are deemed most useful.

For more detailed documentation of each event type, please consult the
[build_event_stream.proto](https://github.com/bazelbuild/bazel/blob/master/src/main/java/com/google/devtools/build/lib/buildeventstream/proto/build_event_stream.proto)
file, as it has embedded documentation.

### TYPE: STARTED

**Google Description:**
> An event indicating the beginning of a new build. Usually, events
> of those type start a new build-event stream. The target pattern requested
> to be build is contained in one of the announced child events; it is an
> invariant that precisely one of the announced child events has a non-empty
> target pattern.

**Example Payload:**
```json
{
  "id": {
    "started": {}
  },
  "children": [
    { "progress": {} },
    { "unstructuredCommandLine": {} },
    { "structuredCommandLine": { "commandLineLabel": "original" } },
    { "structuredCommandLine": { "commandLineLabel": "canonical" } },
    { "structuredCommandLine": { "commandLineLabel": "tool" } },
    { "buildMetadata": {} },
    { "optionsParsed": {} },
    { "workspaceStatus": {} },
    { "pattern": {
        "pattern": [ "//..." ]
      }
    },
    { "buildFinished": {} }
  ],
  "started": {
    "uuid": "b4fa160a-2233-48de-b4d1-463a20c67256",
    "startTimeMillis": "1622343691246",
    "buildToolVersion": "3.7.1",
    "optionsDescription": "--javacopt=-Werror --javacopt=-Xlint:-options --javacopt='--release 11'",
    "command": "build",
    "workingDirectory": "/Users/mbenioff/dev/myrepo",
    "workspaceDirectory": "/Users/mbenioff/dev/myrepo",
    "serverPid": "58316"
  }
}
```

### TYPE: UNSTRUCTURED COMMAND

**Google Description:**
> Event reporting the command-line of the invocation as
> originally received by the server. Note that this is not the command-line
> given by the user, as the client adds information about the invocation,
> like name and relevant entries of rc-files and client environment variables.
> However, it does contain enough information to reproduce the build
> invocation.

**Example Payload:**
Output below redacted, more env variables and bazelrc values will be present.
This payload came from an invocation of `bazel build //...` which can be discerned
from the payload.
```json
{
  "id": { "unstructuredCommandLine": {} },
  "unstructuredCommandLine": {
    "args": [
      "build",
      "--binary_path=/Users/mbenioff/Library/Caches/bazelisk/downloads/bazelbuild/bazel-3.7.1-darwin-x86_64/bin/bazel",
      "--rc_source=/Users/mbenioff/dev/sfdc-bazel/.bazelrc",
      "--default_override=2:run=--action_env=PATH",
      "--default_override=1:test:debug=--test_arg=--node_options=--inspect-brk",
      "--default_override=2:test=--explicit_java_test_deps=true",
      "--default_override=2:build=--javacopt=--release 11",
      "--client_env=TERM_PROGRAM=Apple_Terminal",
      "--client_env=USER=mbenioff",
      "--client_env=PWD=/Users/mbenioff/dev/myrepo",
      "--client_env=JAVA_HOME=/Users/mbenioff/java/openjdk_11.0.9_11.43.54_x64",
      "//..."
    ]
  }
}
```

### TYPE: OPTIONS PARSED

**Google Description:**
> Event reporting on the parsed options, grouped in various ways.

**Example Payload:**
Output below redacted, more env variables and bazelrc values will be present.
The SDK has an alternative approach to gather this information, consider using
that. See *BazelWorkspaceCommandOptions*.

```json
{
  "id": { "optionsParsed": {} },
  "optionsParsed": {
    "startupOptions": [
      "--output_user_root=/var/tmp/_bazel_mbenioff",
      "--output_base=/private/var/tmp/_bazel_mbenioff/d9d40273485d06d9755a220abc6e68f7",
      "--host_jvm_args=-Dtest=one",
      "--host_jvm_args=-Dtest=two",
    ],
    "explicitStartupOptions": [
      "--host_jvm_args=-Dtest=one",
      "--host_jvm_args=-Dtest=two",
    ],
    "cmdLine": [
      "--javacopt=-Werror",
      "--javacopt=-Xlint:-options",
      "--javacopt=--release 11",
    ],
    "invocationPolicy": {}
  }
}
```

### TYPE: PATTERN

**Google Description:**
> Event indicating the expansion of a target pattern.
> The main information is in the chaining part: the id will contain the
> target pattern that was expanded and the children id will contain the
> target or target pattern it was expanded to.

**Example Payload:**
```json
{
  "id": {
    "pattern": {
      "pattern": [
        "//..."
      ]
    }
  },
  "children": [
    { "targetConfigured": { "label": "//foo:lombok" } },
    { "targetConfigured": { "label": "//foo:foo" } },
    { "targetConfigured": { "label": "//foo:foo-test" } }
  ],
  "expanded": {}
}
```

### TYPE: CONFIGURATION

**Google Description:**
> Event reporting details of a given configuration.

**Example Payload:**
```json
{
  "id": {
    "configuration": { "id": "63cc040ed2b86a512099924e698df6e0b9848625e6ca33d9556c5993dccbc2fb" }
  },
  "configuration": {
    "mnemonic": "darwin-fastbuild",
    "platformName": "darwin",
    "cpu": "darwin",
    "makeVariable": {
      "COMPILATION_MODE": "fastbuild",
      "TARGET_CPU": "darwin",
      "GENDIR": "bazel-out/darwin-fastbuild/bin",
      "BINDIR": "bazel-out/darwin-fastbuild/bin"
    }
  }
}
```

### TYPE: PROGRESS

**Google Description:**
> Event summarizing the progress of the build so far. Those
> events are also used to be parents of events where the more logical parent
> event cannot be posted yet as the needed information is not yet complete.

**Example Payload:**
Note that the raw _stderr_ value contains unicode formatting and other control characters.
The SDK has a special parser to strip those characters before your
code interacts with the value.
```json
{
  "id": {
    "progress": { "opaqueCount": 5 }
  },
  "progress": {
    "stderr": "ERROR: /Users/mbenioff/dev/myrepo/foo/BUILD:7:12: Building foo/foo-class.jar (59 source files) and running annotation processors (AnnotationProcessorHider$AnnotationProcessor) failed (Exit 1): java failed: error executing command external/remotejdk11_macos/bin/java -XX:+UseParallelOldGC -XX:-CompactStrings '--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED' ... (remaining 15 argument(s) skipped)\n checking cached actions\nfoo/src/main/java/com/foo/Foo.java:43: error: <identifier> expected\n  bar\n     ^\n[2 / 6] checking cached actions\nINFO: Elapsed time: 2.320s, Critical Path: 2.06s\n[2 / 6] checking cached actions\nINFO: 2 processes: 2 internal.\n[2 / 6] checking cached actions\nFAILED: Build did NOT complete successfully\n"
  }
}
```

### TYPE: TARGET CONFIGURED

**Google Description:**
> Event indicating the completion of a target. The target is specified in the
> id. If the target failed the root causes are provided as children events.

**Example Payload:**

```json
{
  "id": {
    "targetConfigured": {
      "label": "//projects/libs/scone/abstractions:abstractions"
    }
  },
  "children": [
    {
      "targetCompleted": {
        "label": "//projects/libs/scone/abstractions:abstractions",
        "configuration": {
          "id": "1aee508e1d8c40d63ce4bd544a171e81ac2463f0e7d2f7a8dd4d4ddf19a5366e"
        }
      }
    }
  ],
  "configured": {
    "targetKind": "java_library rule"
  }
}
```

### TYPE: TARGET COMPLETED

**Google Description:**
> Event indicating the completion of a target. The target is specified in the
> id. If the target failed the root causes are provided as children events.

**Example Payload:**
This event is complicated, so a reading of
[TargetComplete](https://github.com/bazelbuild/bazel/blob/master/src/main/java/com/google/devtools/build/lib/buildeventstream/proto/build_event_stream.proto#L554)
event documentation is recommended.

Success case:
```json
{
  "id": {
    "targetCompleted": {
      "label": "//foo:foo",
      "configuration": { "id": "63cc040ed2b86a512099924e698df6e0b9848625e6ca33d9556c5993dccbc2fb" }
    }
  },
  "completed": {
    "success": true,
    "outputGroup": [
      {
        "name": "default",
        "fileSets": [ { "id": "2" } ]
      }
    ],
    "importantOutput": [
      {
        "name": "foo/foo.jar",
        "uri": "file:///private/var/tmp/_bazel_mbenioff/8fc74f66fda297c82a847368ee50d6a4/execroot/myrepo/bazel-out/darwin-fastbuild/bin/foo/foo.jar",
        "pathPrefix": [
          "bazel-out", "darwin-fastbuild", "bin"
        ]
      },
      {
        "name": "foo/foo",
        "uri": "file:///private/var/tmp/_bazel_mbenioff/8fc74f66fda297c82a847368ee50d6a4/execroot/myrepo/bazel-out/darwin-fastbuild/bin/foo/foo",
        "pathPrefix": [
          "bazel-out", "darwin-fastbuild", "bin"
        ]
      }
    ]
  }
}
```

Failure case:
```json
{
  "id": {
    "targetCompleted": {
      "label": "//foo:foo",
      "configuration": { "id": "63cc040ed2b86a512099924e698df6e0b9848625e6ca33d9556c5993dccbc2fb" }
    }
  },
  "children": [
    {
      "actionCompleted": {
        "primaryOutput": "bazel-out/darwin-fastbuild/bin/foo/foo-class.jar",
        "label": "//foo:foo",
        "configuration": { "id": "63cc040ed2b86a512099924e698df6e0b9848625e6ca33d9556c5993dccbc2fb" }
      }
    }
  ],
  "completed": {
    "failureDetail": {
      "message": "worker spawn failed for Javac",
      "spawn": {
        "code": "NON_ZERO_EXIT",
        "spawnExitCode": 1
      }
    }
  }
}
```

### TYPE: BUILD FINISHED

**Google Description:**
> Event indicating the end of a build.

**Example Payload:**

Success case:
```json
{
  "id": { "buildFinished": {} },
  "children": [
    { "buildToolLogs": {} },
    { "buildMetrics": {} }
  ],
  "finished": {
    "overallSuccess": true,
    "finishTimeMillis": "1622351858397",
    "exitCode": { "name": "SUCCESS" },
    "anomalyReport": {}
  }
}
```

Failure case:
```json
{
  "id": {
    "buildFinished": {}
  },
  "children": [
    { "buildToolLogs": {} },
    { "buildMetrics": {} }
  ],
  "finished": {
    "finishTimeMillis": "1622353275709",
    "exitCode": {
      "name": "BUILD_FAILURE",
      "code": 1
    },
    "anomalyReport": {}
  }
}
```

### TYPE: BUILD METRICS

An event that is published at the end of a build, with various metrics of the build.

**Example Payload:**
Note that *--bep_publish_used_heap_size_post_build* must be set in *.bazelrc* for memory
metrics to be populated.
```json
{
  "id": {
    "buildMetrics": {}
  },
  "buildMetrics": {
    "actionSummary": { "actionsCreated": "2", "actionsExecuted": "2" },
    "memoryMetrics": { "usedHeapSizePostBuild":"31446304" },
    "packageMetrics": {},
    "timingMetrics": {
      "cpuTimeInMs": "647",
      "wallTimeInMs": "3459",
      "analysisPhaseTimeInMs": "23",
    }
  }
}
```

### TYPE: BUILD TOOL LOGS

An event that is published at the end of a build, with various logs of the build.
This event is not that interesting except it is usually the bearer of the _lastMessage_
property.

**Example Payload:**
```json
{
  "id": {
    "buildToolLogs": {}
  },
  "lastMessage": true,
  "buildToolLogs": {
    "log": [
      {
        "name": "elapsed time",
        "contents": "OS42ODgwMDA="
      },
      {
        "name": "critical path",
        "contents": "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      },
      {
        "name": "process stats",
        "contents": "MSBwcm9jZXNzOiAxIGludGVybmFsLg=="
      },
      {
        "name": "command.profile.gz",
        "uri": "file:///private/var/tmp/_bazel_mbenioff/8fc74f66fda297c82a847368ee50d6a4/command.profile.gz"
      }
    ]
  }
}```

### TYPE: TEST RESULT

There will be a *testResult* event for each test file invoked by the test build.

**Google Description:**
>  Event reporting about an individual test action.

**Example Payload:**
The example below shows a failure. A successful result will have a value of
*PASSED* in the status property.
For test failures, the referenced xml/log files can be used to get more detailed
information for the failure.
```json
{
  "id": {
    "testResult": {
      "label": "//foo:foo-test",
      "run": 1,
      "shard": 1,
      "attempt": 1,
      "configuration": { "id": "63cc040ed2b86a512099924e698df6e0b9848625e6ca33d9556c5993dccbc2fb" }
    }
  },
  "testResult": {
    "testActionOutput": [
      {
        "name": "test.log",
        "uri": "file:///private/var/tmp/_bazel_mbenioff/8fc74f66fda297c82a847368ee50d6a4/execroot/myrepo/bazel-out/darwin-fastbuild/testlogs/foo/foo-test/test.log"
      },
      {
        "name": "test.xml",
        "uri": "file:///private/var/tmp/_bazel_mbenioff/8fc74f66fda297c82a847368ee50d6a4/execroot/myrepo/bazel-out/darwin-fastbuild/testlogs/foo/foo-test/test.xml"
      }
    ],
    "testAttemptDurationMillis": "826",
    "status": "FAILED",
    "testAttemptStartMillisEpoch": "1622353495424",
    "executionInfo": { "strategy": "darwin-sandbox" }
  }
}
```

### TYPE: TEST SUMMARY

There will be a *testResult* event for each test file invoked by the test build.

**Google Description:**
>  Event summarizing a test.

**Example Payload:**
```json
{
  "id": {
    "testSummary": {
      "label": "//foo:foo-test",
      "configuration": {
        "id": "63cc040ed2b86a512099924e698df6e0b9848625e6ca33d9556c5993dccbc2fb"
      }
    }
  },
  "testSummary": {
    "totalRunCount": 1,
    "passed": [
      {
        "uri": "file:///private/var/tmp/_bazel_mbenioff/8fc74f66fda297c82a847368ee50d6a4/execroot/myrepo/bazel-out/darwin-fastbuild/testlogs/foo/foo-test/test.log"
      }
    ],
    "overallStatus": "PASSED",
    "firstStartTimeMillis": "1622354133812",
    "lastStopTimeMillis": "1622354134270",
    "totalRunDurationMillis": "458",
    "runCount": 1
  }
}
```

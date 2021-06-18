#
# Copyright (c) 2017-2021, salesforce.com, inc.
# All rights reserved.
# Licensed under the BSD 3-Clause license.
# For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
#

load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")

repositories = [
    "https://repo1.maven.org/maven2",
]

def bazel_java_sdk_deps():
    maven_install(
        artifacts = [
            "org.slf4j:slf4j-api:1.7.7",

            "junit:junit:4.13",
            "net.bytebuddy:byte-buddy-agent:1.10.18",
            "net.bytebuddy:byte-buddy:1.10.18",
            "org.hamcrest:hamcrest-core:1.3",
            "org.mockito:mockito-core:3.6.28",
            "org.objenesis:objenesis:3.1",
        ],
        excluded_artifacts = [
        ],
        repositories = repositories,
        fetch_sources = True,
        version_conflict_policy = "pinned",
        strict_visibility = True,
        generate_compat_repositories = False,
#        maven_install_json = "@bazel_java_sdk//:maven_install.json",
        resolve_timeout = 1800,
    )

## Contributing to Bazel Java SDK

We welcome contributions.
We do have a [code of conduct](CODE_OF_CONDUCT.md), please read that first.

We have also some key design tenets outlined in the top-level README.
Specifically, before you embark on any coding for the SDK, please be aware:
- Do not bring in any dependency (e.g. guava, spring) without discussing it with us first. We are trying to avoid dependencies.
- We want the SDK code to be very approachable to new learners, so please minimize the use of generics, stream api, lambdas and other advanced coding patterns.

### Please Engage with Us

If you want to implement a major feature, as with any open source project it is worthwhile to engage with us first.
Perhaps we already planned work in that area that might conflict.
Perhaps we won't want that feature implemented directly in the SDK (maybe yours is a feature better as a separate library).
If we discuss ahead of time, we might eliminate any frustration or lost effort.

### SDK Test Framework is Hard to Use

In order to write effective unit tests for the SDK, we (actually, I) wrote a test framework that emulates Bazel.
It is a little complicated and is hard to navigate.
Sorry.

If you want to contribute a bug fix or a feature, and are fighting with the test framework, let me (_plaird_) know.
I wouldn't want you to give up because the tests are too hard to write and/or fix.

### Platform Testing

We support the *bazel_java_sdk* on Mac OS, Linux and Windows.

Because of Windows support, please be mindful of file system operations.
If you are constructing file system paths, do not assume "/" as the separator character.
Look around at *BazelPathHelper* usages to see how we convert Bazel paths ("//a/b/c") to
  file system paths ("a/b/c" or "a\\b\\c").

Ideally, before submitting your PR you will test on all three platforms.
But, we recognize this is a big burden on contributors.
Since we are grateful for your contribution, we are willing to do platform testing for you
  if necessary.
Please let us know what platforms you tested on in your PR comment.

## Contributing to Bazel Java SDK

We welcome contributions.
We do have a [code of conduct](CODE_OF_CONDUCT.md), please read that first.

We have also some key design tenets outlined in the top-level README.
Specifically, before you embark on any coding for the SDK, please be aware:
- Do not bring in any dependency (e.g. guava, spring) without discussing it with us first. We are trying to avoid dependencies.
- We want the SDK code to be very approachable to new learners, so please minimize the use of generics, stream api, lambdas and other advanced coding patterns.


### SDK Test Framework is Hard to Use

In order to write effective unit tests for the SDK, we (actually, I) wrote a test framework that emulates Bazel.
It is a little complicated and is hard to navigate.
Sorry.

If you want to contribute a bug fix or a feature, and are fighting with the test framework, let me (_plaird_) know.
I wouldn't want you to give up because the tests are too hard to write and/or fix.

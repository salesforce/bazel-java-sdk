## Contributing to Bazel Java SDK

We welcome contributions.
We do have a [code of conduct](CODE_OF_CONDUCT.md), please read that first.

We have also some key design tenets outlined in the top-level README.
Specifically, before you embark on any coding for the SDK, please be aware:
- Do not bring in any dependency (e.g. guava, spring) without discussing it with us first. We are trying to avoid dependencies.
- We want the SDK code to be very approachable to new learners, so please minimize the use of generics, stream api, lambdas and other advanced coding patterns.

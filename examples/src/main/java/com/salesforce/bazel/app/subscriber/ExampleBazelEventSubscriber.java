package com.salesforce.bazel.app.subscriber;

import com.salesforce.bazel.sdk.bep.BazelBuildEventSubscriber;
import com.salesforce.bazel.sdk.bep.event.BazelBuildEvent;

public class ExampleBazelEventSubscriber implements BazelBuildEventSubscriber {

    @Override
    public void onEvent(BazelBuildEvent event) {
        System.out.println("Example subscriber received event ["+event.getIndex()+"] "+event.toString());
    }

}

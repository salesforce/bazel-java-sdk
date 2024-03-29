/**
 * Copyright (c) 2019, Salesforce.com, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2016 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.salesforce.bazel.sdk.logging;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.salesforce.bazel.sdk.logging.CaptureLoggerFacade.LogEvent;

/**
 * Test LogHelper
 */
public class LogHelperTest {
    LoggerFacade original;

    @Before
    public void before() {
        original = LoggerFacade.instance();
    }

    @After
    public void after() {
        LoggerFacade.setInstance(original);
    }

    @Test
    public void simple() {
        LogHelper subject = LogHelper.log(getClass());
        final AtomicReference<LogEvent> testEvent = new AtomicReference<>();
        LoggerFacade facade = new CaptureLoggerFacade(event -> {
            testEvent.set(event);
        });
        LoggerFacade.setInstance(facade);
        subject.debug("test {}", 1);
        assertNull(testEvent.get());
        subject.setLevel(LoggerFacade.DEBUG);
        subject.debug("test {}", 1);
        assertEquals("test {}", testEvent.get().message);
        assertEquals(getClass(), testEvent.get().from);
        assertNull(testEvent.get().exception);
        assertArrayEquals(new Object[] { 1 }, testEvent.get().args);
    }

    @Test
    public void logFacadeCanChange() {
        LogHelper subject = LogHelper.log(getClass());
        final AtomicReference<LogEvent> firstEvent = new AtomicReference<>();
        LoggerFacade first = new CaptureLoggerFacade(event -> {
            firstEvent.set(event);
        });
        final AtomicReference<LogEvent> secondEvent = new AtomicReference<>();
        LoggerFacade second = new CaptureLoggerFacade(event -> {
            secondEvent.set(event);
        });
        LoggerFacade.setInstance(first);
        subject.info("first mistake");
        LoggerFacade.setInstance(second);
        subject.info("last mistake");
        assertEquals("first mistake", firstEvent.get().message);
        assertEquals("last mistake", secondEvent.get().message);
    }

    @Test
    public void testFormatting() {
        String result = LoggerFacade.formatMsg(LoggerFacade.class, "abc {} def {}", "ONE", "TWO");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] abc ONE def TWO", result);

        // arg at the beginning
        result = LoggerFacade.formatMsg(LoggerFacade.class, "{} def {} fgh", "ONE", "TWO");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] ONE def TWO fgh", result);

        // args next to each other
        result = LoggerFacade.formatMsg(LoggerFacade.class, "abc {}{} fgh", "ONE", "TWO");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] abc ONETWO fgh", result);

        // only an arg
        result = LoggerFacade.formatMsg(LoggerFacade.class, "{}", "ONE");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] ONE", result);

        // not enough args
        result = LoggerFacade.formatMsg(LoggerFacade.class, "abc {} def {} fgh", "ONE");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] abc ONE def {} fgh", result);

        // extra args
        result = LoggerFacade.formatMsg(LoggerFacade.class, "{} def {} fgh", "ONE", "TWO", "THREE", "FOUR");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] ONE def TWO fgh", result);

        // space in the token pattern
        result = LoggerFacade.formatMsg(LoggerFacade.class, "abc {} def { }", "ONE", "TWO");
        assertEquals("[com.salesforce.bazel.sdk.logging.LoggerFacade] abc ONE def { }", result);
    }

}

/*
 * Copyright (c) 2017 The Microbule Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.microbule.config.core;

import org.junit.Test;
import org.microbule.config.api.Config;
import org.microbule.test.core.MicrobuleTestCase;

public class ConfigUtilsTest extends MicrobuleTestCase {

    @Test
    public void testIsUtilsClass() throws Exception {
        assertIsUtilsClass(ConfigUtils.class);
    }

    @Test
    public void testBootstrapConfig() {
        System.setProperty("microbule.foo.bar", "baz");
        final Config config = ConfigUtils.bootstrapConfig("foo");
        assertEquals("baz", config.value("bar").get());
    }

}
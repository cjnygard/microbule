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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.microbule.config.api.Config;

public final class ConfigUtils {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final int PRIORITY_SYSPROP = -2000;
    public static final int PRIORITY_ENV = -1000;
    public static final int DEFAULT_PRIORITY = 0;
    public static final int PRIORITY_EXTERNAL = 1000;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Config bootstrapConfig(String providerName) {
        return new CompositeConfig(fromProperties(System.getProperties()), new MapConfig(System.getenv()))
                .filtered("microbule", providerName);
    }

    public static Config fromProperties(Properties props) {
        Map<String, String> map = new HashMap<>();
        for (String propertyName : props.stringPropertyNames()) {
            map.put(propertyName, props.getProperty(propertyName));
        }
        return new MapConfig(map);
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    private ConfigUtils() {
    }
}

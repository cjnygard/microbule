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

package org.microbule.tracer.decorator;

import org.microbule.spi.JaxrsServiceDecorator;

public abstract class AbstractTracerDecorator implements JaxrsServiceDecorator{

    public static final String DEFAULT_REQUEST_ID_HEADER = "Microbule-Request-ID";
    public static final String DEFAULT_TRACE_ID_HEADER = "Microbule-Trace-ID";
    public static final String TRACE_ID_KEY = "microbule_trace_id";
    public static final String REQUEST_ID_KEY = "microbule_request_id";
    public static final String TRACE_ID_HEADER_PROP = "traceIdHeader";
    public static final String REQUEST_ID_HEADER_PROP = "requestIdHeader";

    public static final String DECORATOR_NAME = "tracer";

    @Override
    public String name() {
        return DECORATOR_NAME;
    }
}

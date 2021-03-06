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

package org.microbule.errormap.core;

import java.lang.reflect.Constructor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.microbule.errormap.spi.ErrorResponseStrategy;

public abstract class AbstractErrorResponseStrategy implements ErrorResponseStrategy {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    protected static final String NEWLINE = "\n";

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    protected RuntimeException createException(Response response, String errorMessage) {
        Class<? extends WebApplicationException> exceptionClass = WebApplicationExceptions.getWebApplicationExceptionClass(response);
        try {
            final Constructor<? extends WebApplicationException> ctor = exceptionClass.getConstructor(String.class, Response.class);
            return ctor.newInstance(errorMessage, response);
        } catch (ReflectiveOperationException e) {
            return new WebApplicationException(errorMessage, response);
        }
    }
}

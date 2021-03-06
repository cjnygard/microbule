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

package org.microbule.gson.provider;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.microbule.config.api.Config;
import org.microbule.container.core.SimpleContainer;
import org.microbule.gson.api.GsonService;
import org.microbule.gson.core.DefaultGsonService;
import org.microbule.spi.JaxrsProxyDecorator;
import org.microbule.spi.JaxrsServerDecorator;
import org.microbule.spi.JaxrsServiceDescriptor;
import org.microbule.test.server.JaxrsServerTestCase;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public class GsonServerProviderTest extends JaxrsServerTestCase<PersonService> {

    @Mock
    private PersonService serviceImpl;

    @Override
    protected PersonService createImplementation() {
        return serviceImpl;
    }

    @Override
    protected void addBeans(SimpleContainer container) {
        GsonService gsonService = new DefaultGsonService(container);
        container.addBean(new JaxrsServerDecorator() {
            @Override
            public String name() {
                return "gsonServer";
            }

            @Override
            public void decorate(JaxrsServiceDescriptor descriptor, Config config) {
                descriptor.addProvider(new GsonProvider(gsonService, e -> new JsonRequestParsingException(e)));
            }
        });

        container.addBean(new JaxrsProxyDecorator() {
            @Override
            public String name() {
                return "gsonProxy";
            }

            @Override
            public void decorate(JaxrsServiceDescriptor descriptor, Config config) {
                descriptor.addProvider(new GsonProvider(gsonService, e -> new JsonResponseParsingException(e)));
            }
        });
    }

    @Test
    public void testGetSize() {
        assertEquals(-1, new GsonProvider(null, null).getSize(null, null, null, null, null));
    }

    @Test
    public void testWithJsonSyntaxException() {
        final Response response = createWebTarget().path("people").request().post(Entity.entity("{{{}", MediaType.APPLICATION_JSON_TYPE));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testBeanSerialization() {
        when(serviceImpl.findPerson("Slappy", "White")).thenReturn(new Person("Slappy", "White"));
        final Person person = createProxy().findPerson("Slappy", "White");
        assertEquals("Slappy", person.getFirstName());
        assertEquals("White", person.getLastName());
    }

    @Test
    public void testGenericTypeSerialization() {
        when(serviceImpl.all()).thenReturn(Lists.newArrayList(new Person("Slappy", "White")));
        final List<Person> persons = createProxy().all();
        assertEquals(1, persons.size());
    }
}
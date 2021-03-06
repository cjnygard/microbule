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

package org.microbule.test.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduit;
import org.junit.After;
import org.junit.Before;
import org.microbule.api.JaxrsConfigService;
import org.microbule.api.JaxrsServer;
import org.microbule.config.api.Config;
import org.microbule.config.core.MapConfig;
import org.microbule.container.core.SimpleContainer;
import org.microbule.core.DefaultJaxrsProxyFactory;
import org.microbule.core.DefaultJaxrsServerFactory;
import org.microbule.core.DefaultJaxrsServiceDiscovery;
import org.microbule.scheduler.core.DefaultSchedulerService;
import org.microbule.test.core.MockObjectTestCase;
import org.microbule.util.reflect.Types;

public abstract class JaxrsServerTestCase<T> extends MockObjectTestCase implements JaxrsConfigService {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final String BASE_ADDRESS_PATTERN = "http://localhost:%d/%s";

    private JaxrsServer server;
    private String baseAddress;
    private final SimpleContainer container = new SimpleContainer();
    private DefaultJaxrsProxyFactory proxyFactory;

//----------------------------------------------------------------------------------------------------------------------
// Abstract Methods
//----------------------------------------------------------------------------------------------------------------------

    protected abstract T createImplementation();

//----------------------------------------------------------------------------------------------------------------------
// JaxrsConfigService Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Config createConfig(String... path) {
        MapConfig config = new MapConfig();
        configureGlobalConfig(config);
        return config;
    }

    @Override
    public final <I> Config createProxyConfig(Class<I> serviceInterface, String serviceName) {
        MapConfig config = new MapConfig();
        configureProxy(config);
        config.addValue(DefaultJaxrsServiceDiscovery.PROXY_ADDRESS_PROP, baseAddress);
        return config;
    }

    @Override
    public final <I> Config createServerConfig(Class<I> serviceInterface, String serviceName) {
        MapConfig config = new MapConfig();
        configureServer(config);
        config.addValue(DefaultJaxrsServerFactory.SERVER_ADDRESS_PROP, baseAddress);
        return config;
    }

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public String getBaseAddress() {
        return baseAddress;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    protected void addBeans(SimpleContainer container) {
        // Do nothing!
    }

    protected int calculatePort() {
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLocalHost())) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to allocate port for test case.", e);
        }
    }

    protected void configureGlobalConfig(MapConfig globalConfig) {
        // Do nothing!
    }

    protected void configureProxy(MapConfig proxyConfig) {
        // Do nothing!
    }

    protected void configureServer(MapConfig serverConfig) {
        // Do nothing!
    }

    protected String createBaseAddress() {
        return String.format(BASE_ADDRESS_PATTERN, calculatePort(), getClass().getSimpleName());
    }

    protected T createProxy() {
        return proxyFactory.createProxy(getServiceInterface());
    }
    
    private Class<T> getServiceInterface() {
        return Types.getTypeParameter(getClass(), JaxrsServerTestCase.class, 0);
    }

    protected WebTarget createWebTarget() {
        return ClientBuilder.newClient().target(getBaseAddress()).property(AsyncHTTPConduit.USE_ASYNC, Boolean.TRUE);
    }

    @After
    public void shutdownServer() {
        if (server != null) {
            server.shutdown();
        }
    }

    @Before
    public void startServer() {
        addBeans(container);
        final DefaultJaxrsServerFactory factory = new DefaultJaxrsServerFactory(container, this);
        proxyFactory = new DefaultJaxrsProxyFactory(container, this, new DefaultSchedulerService(1));
        container.initialize();

        baseAddress = createBaseAddress();
        server = factory.createJaxrsServer(getServiceInterface(), createImplementation());
    }
}

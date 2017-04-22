package org.microbule.api;

import org.microbule.config.api.Config;

public interface JaxrsProxyFactory {
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    String ADDRESS_PROP = "proxyAddress";

    /**
     * Creates a JAX-RS proxy which supports the JAX-RS annotated <code>serviceInterface</code>.
     *
     * @param serviceInterface the service interface
     * @param config           the configuration
     * @param <T>              the proxy type
     * @return the proxy
     */
    <T> T createProxy(Class<T> serviceInterface, Config config);
}

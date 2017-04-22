package org.microbule.gson.decorator;

import org.microbule.config.api.Config;
import org.microbule.gson.api.GsonService;
import org.microbule.spi.JaxrsProxyDecorator;
import org.microbule.spi.JaxrsServerDecorator;
import org.microbule.spi.JaxrsServiceDescriptor;

public class GsonDecorator implements JaxrsProxyDecorator, JaxrsServerDecorator {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private final GsonService gsonService;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public GsonDecorator(GsonService gsonService) {
        this.gsonService = gsonService;
    }

//----------------------------------------------------------------------------------------------------------------------
// JaxrsServiceDecorator Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void decorate(JaxrsServiceDescriptor proxy, Config config) {
        proxy.addProvider(new GsonProvider(gsonService));
    }
}
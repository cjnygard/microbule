package org.microbule.config.core;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import org.apache.commons.lang3.StringUtils;
import org.microbule.config.api.Config;
import org.microbule.config.api.ConfigService;
import org.microbule.config.spi.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigService implements ConfigService {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final char COMMA_SEPARATOR = ',';
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigService.class);
    private final List<String> providerNames;
    private final Retryer<ConfigProvider> retryer;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public AbstractConfigService(String providerNamesCsv, long waitDuration, TimeUnit waitUnit) {
        this(Stream.of(StringUtils.split(providerNamesCsv, COMMA_SEPARATOR)).map(String::trim).collect(Collectors.toList()), waitDuration, waitUnit);
    }

    public AbstractConfigService(List<String> providerNames, long waitDuration, TimeUnit waitUnit) {
        this.retryer = RetryerBuilder
                .<ConfigProvider>newBuilder()
                .retryIfResult(Predicates.isNull())
                .withWaitStrategy(WaitStrategies.fibonacciWait())
                .withStopStrategy(StopStrategies.stopAfterDelay(waitDuration, waitUnit))
                .build();
        this.providerNames = providerNames;
    }

//----------------------------------------------------------------------------------------------------------------------
// Abstract Methods
//----------------------------------------------------------------------------------------------------------------------

    protected abstract ConfigProvider lookupProvider(String name);

//----------------------------------------------------------------------------------------------------------------------
// ConfigService Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Config getProxyConfig(Class<?> serviceInterface) {
        return getProviderConfigs(provider -> provider.getProxyConfig(serviceInterface));
    }

    @Override
    public Config getServerConfig(Class<?> serviceInterface) {
        return getProviderConfigs(provider -> provider.getServerConfig(serviceInterface));
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    private Config getProviderConfigs(Function<ConfigProvider, Config> fn) {
        final List<Config> members = providerNames.stream().map(this::retryProvider).map(fn).collect(Collectors.toList());
        return new CompositeConfig(members);
    }

    private ConfigProvider retryProvider(String name) {
        try {
            return retryer.call(() -> lookupProvider(name));
        } catch (ExecutionException | RetryException e) {
            LOGGER.warn("Provider '{}' not found, using empty config provider instead.", name);
            return EmptyConfigProvider.INSTANCE;
        }
    }
}
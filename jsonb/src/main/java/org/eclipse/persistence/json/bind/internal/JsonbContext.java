/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.adapter.AdapterMatcher;
import org.eclipse.persistence.json.bind.internal.adapter.JsonbAdapterInfo;
import org.eclipse.persistence.json.bind.internal.naming.DefaultNamingStrategies;
import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Context holding common objects in {@link ThreadLocal}
 *
 * @author Roman Grigoriadi
 */
public class JsonbContext {

    private static final ThreadLocal<JsonbContext> instances = new ThreadLocal<>();

    private final JsonbConfig jsonbConfig;

    private final MappingContext mappingContext;

    private final PropertyVisibilityStrategy propertyVisibilityStrategy;

    private final PropertyNamingStrategy propertyNamingStrategy;

    private final List<JsonbAdapterInfo> adapters;

    /**
     * Creates and initialize context.
     *
     * @param jsonbConfig jsonb jsonbConfig not null
     * @param mappingContext mapping context not null
     */
    public JsonbContext(JsonbConfig jsonbConfig, MappingContext mappingContext) {
        Objects.requireNonNull(jsonbConfig);
        Objects.requireNonNull(mappingContext);
        this.jsonbConfig = jsonbConfig;
        this.mappingContext = mappingContext;
        this.propertyNamingStrategy = resolvePropertyNamingStrategy();
        this.propertyVisibilityStrategy = resolvePropertyVisibilityStrategy();
        this.adapters = resolveAdapters();
    }

    private PropertyNamingStrategy resolvePropertyNamingStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY);
        if (!property.isPresent()) {
            return null;
        }
        String namingStrategyName = (String) property.get();
        final PropertyNamingStrategy foundNamingStrategy = DefaultNamingStrategies.getStrategy(namingStrategyName);
        if (foundNamingStrategy == null) {
            throw new JsonbException("No property naming strategy was found for: " + namingStrategyName);
        }
        return foundNamingStrategy;
    }

    private PropertyVisibilityStrategy resolvePropertyVisibilityStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_VISIBILITY_STRATEGY);
        if (!property.isPresent()) {
            return null;
        }
        final Object propertyVisibilityStrategy = property.get();
        if (!(propertyVisibilityStrategy instanceof PropertyVisibilityStrategy)) {
            throw new JsonbException("JsonbConfig.PROPERTY_VISIBILITY_STRATEGY must be instance of " + PropertyVisibilityStrategy.class);
        }
        return (PropertyVisibilityStrategy) propertyVisibilityStrategy;
    }

    private List<JsonbAdapterInfo> resolveAdapters() {
        return AdapterMatcher.getInstance().parseRegisteredAddapters((JsonbAdapter<?, ?>[]) jsonbConfig.getProperty(JsonbConfig.ADAPTERS).orElse(new JsonbAdapter<?,?>[]{}));
    }

    /**
     * Instance of Jsonb jsonbConfig.
     * @return jsonb jsonbConfig
     */
    public static JsonbConfig getConfig() {
        return getInstance().jsonbConfig;
    }

    /**
     * Instance of MappingContext.
     * @return mapping context
     */
    public static MappingContext getMappingContext() {
        return getInstance().mappingContext;
    }

    /**
     * Property visibility strategy.
     * @return strategy for property visibility
     */
    public static PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
        return getInstance().propertyVisibilityStrategy;
    }

    public static List<JsonbAdapterInfo> getAdapters() {
        return getInstance().adapters;
    }

    public static PropertyNamingStrategy getPropertyNamingStrategy() {
        return getInstance().propertyNamingStrategy;
    }

    static void setInstance(JsonbContext context) {
        if (instances.get() != null) {
            throw new IllegalStateException("JsonbContext already set!");
        }
        instances.set(context);
    }

    static void removeInstance() {
        if (instances.get() == null) {
            throw new IllegalStateException("JsonbContext is not set!");
        }
        instances.remove();
    }

    /**
     * Instance of this context.
     * @return instance
     */
    static JsonbContext getInstance() {
        return instances.get();
    }


}

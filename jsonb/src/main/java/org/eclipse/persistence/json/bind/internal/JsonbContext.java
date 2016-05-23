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

import org.eclipse.persistence.json.bind.internal.cdi.JsonbComponentInstanceCreator;
import org.eclipse.persistence.json.bind.internal.naming.DefaultNamingStrategies;
import org.eclipse.persistence.json.bind.internal.naming.PropertyNamingStrategy;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.spi.JsonProvider;
import java.util.Objects;
import java.util.Optional;

/**
 * Context holding effectively immutable objects per JSONB configuration.
 * Thread safe
 *
 * @author Roman Grigoriadi
 */
public class JsonbContext {

    private final JsonbConfig jsonbConfig;

    private final MappingContext mappingContext;

    private final JsonbComponentInstanceCreator componentInstanceCreator;

    private final PropertyVisibilityStrategy propertyVisibilityStrategy;

    private final PropertyNamingStrategy propertyNamingStrategy;

    private final JsonProvider jsonProvider;

    private final ComponentMatcher componentMatcher;

    /**
     * Creates and initialize context.
     *
     * @param componentInstanceCreator implementation for creating components such as custom adapters or strategies
     * @param mappingContext mapping context not null
     * @param jsonbConfig jsonb jsonbConfig not null
     * @param jsonProvider provider of JSONP
     */
    public JsonbContext(MappingContext mappingContext, JsonbConfig jsonbConfig, JsonbComponentInstanceCreator componentInstanceCreator, JsonProvider jsonProvider) {
        Objects.requireNonNull(jsonbConfig);
        Objects.requireNonNull(mappingContext);
        Objects.requireNonNull(componentInstanceCreator);
        this.jsonbConfig = jsonbConfig;
        this.mappingContext = mappingContext;
        this.componentInstanceCreator = componentInstanceCreator;
        this.propertyNamingStrategy = resolvePropertyNamingStrategy();
        this.propertyVisibilityStrategy = resolvePropertyVisibilityStrategy();
        this.jsonProvider = jsonProvider;
        this.componentMatcher = new ComponentMatcher();
        this.componentMatcher.init(this);
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


    /**
     * Instance of Jsonb jsonbConfig.
     * @return jsonb jsonbConfig
     */
    public JsonbConfig getConfig() {
        return jsonbConfig;
    }

    /**
     * Instance of MappingContext.
     * @return mapping context
     */
    public MappingContext getMappingContext() {
        return mappingContext;
    }

    /**
     * Property visibility strategy.
     * @return strategy for property visibility
     */
    public PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
        return propertyVisibilityStrategy;
    }

    /**
     * Property naming strategy.
     * @return strategy for property naming.
     */
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return propertyNamingStrategy;
    }

    /**
     * Provider of JSONP implementation.
     * @return JSONP provider.
     */
    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }

    /**
     * Implementation creating instances of user components used by JSONB, such as adapters and strategies.
     *
     * @return instance creator
     */
    public JsonbComponentInstanceCreator getComponentInstanceCreator() {
        return componentInstanceCreator;
    }

    /**
     * Component matcher for lookup of (de)serializers and adapters.
     * @return component matcher
     */
    public ComponentMatcher getComponentMatcher() {
        return componentMatcher;
    }
}

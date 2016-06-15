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
import org.eclipse.persistence.json.bind.internal.internalOrdering.AnyOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.LexicographicalOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.PropOrderStrategy;
import org.eclipse.persistence.json.bind.internal.internalOrdering.ReverseOrderStrategy;
import org.eclipse.persistence.json.bind.internal.naming.DefaultNamingStrategies;
import org.eclipse.persistence.json.bind.internal.naming.IdentityStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.serializer.JsonbDateFormatter;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.config.BinaryDataStrategy;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.spi.JsonProvider;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    private final Map<String, PropOrderStrategy> orderStrategies;

    private final JsonbDateFormatter dateFormatter;

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
        this.orderStrategies = initOrderStrategies();
        this.componentMatcher = new ComponentMatcher();
        this.componentMatcher.init(this);
        this.dateFormatter = initDateFormatter();
    }

    private Map<String, PropOrderStrategy> initOrderStrategies() {
        Map<String, PropOrderStrategy> strategies = new HashMap<>();
        strategies.put(PropertyOrderStrategy.LEXICOGRAPHICAL, new LexicographicalOrderStrategy());
        strategies.put(PropertyOrderStrategy.REVERSE, new ReverseOrderStrategy());
        strategies.put(PropertyOrderStrategy.ANY, new AnyOrderStrategy());
        return Collections.unmodifiableMap(strategies);
    }

    private PropertyNamingStrategy resolvePropertyNamingStrategy() {
        final Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.PROPERTY_NAMING_STRATEGY);
        if (!property.isPresent()) {
            return new IdentityStrategy();
        }
        Object propertyNamingStrategy = property.get();
        if (propertyNamingStrategy instanceof String) {
            String namingStrategyName = (String) propertyNamingStrategy;
            final PropertyNamingStrategy foundNamingStrategy = DefaultNamingStrategies.getStrategy(namingStrategyName);
            if (foundNamingStrategy == null) {
                throw new JsonbException("No property naming strategy was found for: " + namingStrategyName);
            }
            return foundNamingStrategy;
        }
        if (!(propertyNamingStrategy instanceof PropertyNamingStrategy)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.PROPERTY_NAMING_STRATEGY_INVALID));
        }
        return (PropertyNamingStrategy) property.get();
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

    /**
     * Property order strategies for serializers.
     *
     * @return property order strategies
     */
    public Map<String, PropOrderStrategy> getOrderStrategies() {
        return orderStrategies;
    }

    /**
     * Checks for binary data strategy to use.
     *
     * @return binary data strategy
     */
    public  String getBinaryDataStrategy() {
        final Optional<Boolean> iJson = jsonbConfig.getProperty(JsonbConfig.STRICT_IJSON).map((obj->(Boolean)obj));
        if (iJson.isPresent() && iJson.get()) {
            return BinaryDataStrategy.BASE_64_URL;
        }
        final Optional<String> strategy = jsonbConfig.getProperty(JsonbConfig.BINARY_DATA_STRATEGY).map((obj) -> (String) obj);
        return strategy.orElse(BinaryDataStrategy.BYTE);
    }

    private JsonbDateFormatter initDateFormatter() {
        final String dateFormat = getGlobalConfigJsonbDateFormat();
        final Locale locale = getConfigLocale();
        //In case of java.time singleton formats will be used inside related (de)serializers,
        //in case of java.util.Date and Calendar new instances will be created TODO PERF consider synchronization
        if (JsonbDateFormat.DEFAULT_FORMAT.equals(dateFormat) || JsonbDateFormat.TIME_IN_MILLIS.equals(dateFormat)) {
            return new JsonbDateFormatter(dateFormat, locale.toLanguageTag());
        }
        //if possible create shared instance of java.time formatter.
        return new JsonbDateFormatter(DateTimeFormatter.ofPattern(dateFormat, locale), dateFormat, locale.toLanguageTag());
    }

    /**
     * DateFormatter of JsonbConfig.
     * @return date formatter
     */
    private String getGlobalConfigJsonbDateFormat() {
        final Optional<Object> formatProperty = jsonbConfig.getProperty(JsonbConfig.DATE_FORMAT);
        return formatProperty.map(f -> {
            if (!(f instanceof String)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.DATE_FORMAT, String.class.getSimpleName()));
            }
            return (String) f;
        }).orElse(JsonbDateFormat.DEFAULT_FORMAT);
    }

    public Locale getLocale(String locale) {
        if (locale.equals(JsonbDateFormat.DEFAULT_LOCALE)) {
            return getConfigLocale();
        }
        return Locale.forLanguageTag(locale);
    }

    /**
     * Locale of JsonbConfig.
     * @return locale
     */
    public Locale getConfigLocale() {
        final Optional<Object> localeProperty = jsonbConfig.getProperty(JsonbConfig.LOCALE);
        return  localeProperty.map(loc -> {
            if (!(loc instanceof Locale)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.LOCALE, Locale.class.getSimpleName()));
            }
            return (Locale) loc;
        }).orElseGet(Locale::getDefault);
    }

    /**
     * Instantiated shared config date formatter.
     * @return date formatter
     */
    public JsonbDateFormatter getConfigDateFormatter() {
        return dateFormatter;
    }
}

/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.stream.JsonGenerator;
import java.util.HashMap;
import java.util.Map;

/**
 * Common parent for marshalling and unmarshalling shared logic.
 *
 * @author Roman Grigoriadi
 */
public abstract class JsonTextProcessor {


    protected static final String DOUBLE_INFINITY = "INFINITY";
    protected static final String DOUBLE_NAN = "NaN";
    protected static final String NULL = "null";

    protected final MappingContext mappingContext;

    protected final TypeConverter converter;

    protected final JsonbConfig jsonbConfig;

    /**
     * Parent instance for marshaller and unmarshaller.
     *
     * @param mappingContext class mapping
     * @param jsonbConfig    jsonb config
     */
    public JsonTextProcessor(MappingContext mappingContext, JsonbConfig jsonbConfig) {
        this.mappingContext = mappingContext;
        this.converter = ConvertersMapTypeConverter.getInstance();
        this.jsonbConfig = jsonbConfig;
    }

    /**
     * Propagates properties from JsonbConfig to JSONP generator / parser factories.
     *
     * @param jsonbConfig jsonb config
     * @return properties for JSONP generator / parser
     */
    protected Map<String, ?> createJsonpProperties(JsonbConfig jsonbConfig) {
        final Map<String, Object> factoryProperties = new HashMap<>();
        //JSONP 1.0 actually ignores the value, just checks the key is present. Only set if JsonbConfig.FORMATTING is true.
        jsonbConfig.getProperty(JsonbConfig.FORMATTING).ifPresent(value->{
            if (!(value instanceof Boolean)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_FORMATTING_ILLEGAL_VALUE));
            }
            if ((Boolean) value) {
                factoryProperties.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
            }
        });
        return factoryProperties;
    }
}

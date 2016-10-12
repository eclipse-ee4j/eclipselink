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
 ******************************************************************************/

package org.eclipse.persistence.json.bind.model;

import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.adapter.DeserializerBinding;
import org.eclipse.persistence.json.bind.internal.adapter.SerializerBinding;
import org.eclipse.persistence.json.bind.internal.serializer.JsonbDateFormatter;
import org.eclipse.persistence.json.bind.internal.serializer.JsonbNumberFormatter;

/**
 * Customization configuration for class or field.
 * Configuration parsed from annotation is put here.
 *
 * @author Roman Grigoriadi
 */
public abstract class Customization {

    private final AdapterBinding adapterBinding;

    private final SerializerBinding serializerBinding;

    private final DeserializerBinding deserializerBinding;

    private final boolean nillable;

    private final boolean jsonbTransient;

    private final JsonbDateFormatter dateTimeFormatter;

    private final JsonbNumberFormatter numberFormat;


    /**
     * Copies properties from builder an creates immutable instance.
     * @param builder not null
     */
    public Customization(CustomizationBuilder builder) {
        this.nillable = builder.isNillable();
        this.jsonbTransient = builder.isJsonbTransient();
        this.dateTimeFormatter = builder.getDateFormatter();
        this.numberFormat = builder.getNumberFormat();
        this.adapterBinding = builder.getAdapterInfo();
        this.serializerBinding = builder.getSerializerBinding();
        this.deserializerBinding = builder.getDeserializerBinding();
    }

    /**
     * Copy constructor.
     *
     * @param other other customizaton instance
     */
    public Customization(Customization other) {
        this.nillable = other.isNillable();
        this.jsonbTransient = other.isJsonbTransient();
        this.dateTimeFormatter = other.getDateTimeFormatter();
        this.numberFormat = other.getNumberFormat();
        this.adapterBinding = other.getAdapterBinding();
        this.serializerBinding = other.getSerializerBinding();
        this.deserializerBinding = other.getDeserializerBinding();
    }

    /**
     * Marshall null values to JSON.
     *
     * @return if true marshalling null values is active
     */
    public boolean isNillable() {
        return nillable;
    }

    /**
     * Skip marshalling / unmarshalling for this customization.
     * Works as java "transient" keyword.
     */
    public boolean isJsonbTransient() {

        return jsonbTransient;
    }

    /**
     * Date formatter for formatting dates.
     * If not set defaulted to javax.json.bind.annotation.JsonbDateFormat.DEFAULT_FORMAT.
     * @return date format
     */
    public JsonbDateFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    /**
     * Number format for formatting numbers.
     * @return number format
     */
    public JsonbNumberFormatter getNumberFormat() {
        return numberFormat;
    }


    /**
     * Adapter wrapper class with resolved generic information.
     *
     * @return adapter wrapper
     */
    public AdapterBinding getAdapterBinding() {
        return adapterBinding;
    }

    /**
     * Serializer wrapper with resolved generic info.
     *
     * @return serialzier wrapper
     */
    public SerializerBinding getSerializerBinding() {
        return serializerBinding;
    }

    /**
     * Deserializer wrapper with resolved generic info.
     *
     * @return deserialzier wrapper
     */
    public DeserializerBinding getDeserializerBinding() {
        return deserializerBinding;
    }
}

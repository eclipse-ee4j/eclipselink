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

package org.eclipse.persistence.json.bind.internal.adapter;

import java.lang.reflect.Type;

/**
 * Wrapper holding singleton instances of user defined components - Adapters, (De)Serializers.
 * @author Roman Grigoriadi
 */
public class ComponentBindings {

    private final Type bindingType;

    private final SerializerBinding serializer;

    private final DeserializerBinding deserializer;

    private final AdapterBinding adapterInfo;

    /**
     * Construct empty bindings for a given type.
     * @param bindingType type components are bound to
     */
    public ComponentBindings(Type bindingType) {
        this(bindingType, null, null, null);
    }

    /**
     * Construct populated bindings for a given type.
     * @param bindingType type components are bound to
     */
    public ComponentBindings(Type bindingType, SerializerBinding serializer, DeserializerBinding deserializer, AdapterBinding adapter) {
        this.bindingType = bindingType;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.adapterInfo = adapter;
    }

    /**
     * Type to which components are bound.
     * @return bound type
     */
    public Type getBindingType() {
        return bindingType;
    }

    /**
     * Serializer if any.
     * @return serializer
     */
    public SerializerBinding getSerializer() {
        return serializer;
    }

    /**
     * Deserializer if any.
     * @return deserializer
     */
    public DeserializerBinding getDeserializer() {
        return deserializer;
    }

    /**
     * Adapter info if any.
     * @return adapterInfo
     */
    public AdapterBinding getAdapterInfo() {
        return adapterInfo;
    }

}

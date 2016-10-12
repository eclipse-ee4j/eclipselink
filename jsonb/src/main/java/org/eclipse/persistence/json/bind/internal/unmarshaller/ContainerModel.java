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

package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.model.Customization;
import org.eclipse.persistence.json.bind.model.JsonBindingModel;
import org.eclipse.persistence.json.bind.model.JsonContext;

import java.lang.reflect.Type;

/**
 * Binding model for collection like types.
 * Model provides a value type of a collection and its customization. If for example this model
 * is instantiated for {@code List<MyPojo>}, it will contain {@code MyPojo} value type and customization parsed
 * from annotations of {@code MyPojo} class.
 *
 * @author Roman Grigoriadi
 */
public class ContainerModel implements JsonBindingModel {

    private final JsonContext context;

    private final String writeName;

    private final Type valueRuntimeType;

    private final Customization customization;

    /**
     * Construct model.
     *
     * @param valueRuntimeType collection or map value type
     * @param customization customization parsed from value type
     * @param context Object context used for serialization
     * @param writeName write name used for serialization
     */
    public ContainerModel(Type valueRuntimeType, Customization customization, JsonContext context, String writeName) {
        this.valueRuntimeType = valueRuntimeType;
        this.customization = customization;
        this.context = context;
        this.writeName = writeName;
    }
    /**
     * Construct model.
     *
     * @param valueRuntimeType collection or map value type
     * @param customization customization parsed from value type
     * @param context Object context used for serialization
     */
    public ContainerModel(Type valueRuntimeType, Customization customization, JsonContext context) {
        this.valueRuntimeType = valueRuntimeType;
        this.customization = customization;
        this.context = context;
        this.writeName = null;
    }

    /**
     * Minimal constructor.
     *
     * @param valueRuntimeType collection or map value type
     * @param customization customization parsed from value type
     */
    public ContainerModel(Type valueRuntimeType, Customization customization) {
        this.valueRuntimeType = valueRuntimeType;
        this.customization = customization;
        this.context = null;
        this.writeName = null;
    }

    /**
     * Introspected customization of a property or class.
     *
     * @return immutable property customization
     */
    @Override
    public Customization getCustomization() {
        return customization;
    }

    /**
     * Class of a property, either bean property type or collection / array component type.
     *
     * @return class type
     */
    @Override
    public Type getType() {
        return valueRuntimeType;
    }

    /**
     * Name of json key that will be written by marshaller.
     *
     * @return
     */
    @Override
    public String getWriteName() {
        return writeName;
    }

    /**
     * Current context of json generator.
     *
     * @return context
     */
    @Override
    public JsonContext getContext() {
        return context;
    }

}

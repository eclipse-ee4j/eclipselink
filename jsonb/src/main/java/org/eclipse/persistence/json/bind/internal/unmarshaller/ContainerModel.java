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

import java.lang.reflect.Type;

/**
 * Binding model for collection like types
 * @author Roman Grigoriadi
 */
public class ContainerModel implements JsonBindingModel {

    private final Type valueRuntimeType;

    private final Customization customization;

    public ContainerModel(Type valueRuntimeType, Customization customization) {
        this.valueRuntimeType = valueRuntimeType;
        this.customization = customization;
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

}

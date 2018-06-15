/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.jpa.config.Property;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PropertyImpl extends MetadataImpl<PropertyMetadata> implements Property {

    public PropertyImpl() {
        super(new PropertyMetadata());
    }

    @Override
    public Property setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public Property setValue(String value) {
        getMetadata().setValue(value);
        return this;
    }

    @Override
    public Property setValueType(String valueType) {
        getMetadata().setValueTypeName(valueType);
        return this;
    }

}

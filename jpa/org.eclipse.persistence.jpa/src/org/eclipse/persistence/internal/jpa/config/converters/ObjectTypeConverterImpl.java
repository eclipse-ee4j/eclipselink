/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.converters;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConversionValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.jpa.config.ConversionValue;
import org.eclipse.persistence.jpa.config.ObjectTypeConverter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class ObjectTypeConverterImpl extends MetadataImpl<ObjectTypeConverterMetadata> implements ObjectTypeConverter {

    public ObjectTypeConverterImpl() {
        super(new ObjectTypeConverterMetadata());
        getMetadata().setConversionValues(new ArrayList<ConversionValueMetadata>());
    }

    public ConversionValue addConversionValue() {
        ConversionValueImpl conversionValue = new ConversionValueImpl();
        getMetadata().getConversionValues().add(conversionValue.getMetadata());
        return conversionValue;
    }

    public ObjectTypeConverter setDataType(String dataType) {
        getMetadata().setDataTypeName(dataType);
        return this;
    }

    public ObjectTypeConverter setDefaultObjectValue(String defaultObjectValue) {
        getMetadata().setDefaultObjectValue(defaultObjectValue);
        return this;
    }

    public ObjectTypeConverter setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public ObjectTypeConverter setObjectType(String objectType) {
        getMetadata().setObjectTypeName(objectType);
        return this;
    }

}

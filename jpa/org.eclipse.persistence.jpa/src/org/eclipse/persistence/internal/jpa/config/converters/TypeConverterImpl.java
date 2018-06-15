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
package org.eclipse.persistence.internal.jpa.config.converters;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;
import org.eclipse.persistence.jpa.config.TypeConverter;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class TypeConverterImpl extends MetadataImpl<TypeConverterMetadata> implements TypeConverter {

    public TypeConverterImpl() {
        super(new TypeConverterMetadata());
    }

    @Override
    public TypeConverter setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public TypeConverter setDataType(String dataType) {
        getMetadata().setDataTypeName(dataType);
        return this;
    }

    @Override
    public TypeConverter setObjectType(String objectType) {
        getMetadata().setObjectTypeName(objectType);
        return this;
    }

}

/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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

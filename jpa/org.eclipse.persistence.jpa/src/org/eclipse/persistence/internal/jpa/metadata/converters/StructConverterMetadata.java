/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.sql.Types;

import org.eclipse.persistence.config.StructConverterType;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;


import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * INTERNAL:
 * Place holder for a StructConverter
 * 
 * This class will allow a StructConverter to be added to a Session through 
 * annotations when defined with the StructConverter annotation.
 */
public class StructConverterMetadata extends AbstractConverterMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private String m_converter;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public StructConverterMetadata() {
        super("<struct-converter>");
    }
    
    /**
     * INTERNAL:
     */
    public StructConverterMetadata(MetadataAnnotation structConverter, MetadataAccessibleObject accessibleObject) {
        super(structConverter, accessibleObject);
        
        setConverter((String) structConverter.getAttribute("converter"));
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof StructConverterMetadata) {
            StructConverterMetadata structConverter = (StructConverterMetadata) objectToCompare;
            return valuesMatch(m_converter, structConverter.getConverter());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConverter() {
        return m_converter;
    }
    
    /**
     * INTERNAL:
     */
    public String getConverterClassName(){
        if (getConverter().equals(StructConverterType.JGeometry)) {
            return "org.eclipse.persistence.platform.database.oracle.converters.JGeometryConverter";
        }
        
        return getConverter();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isStructConverter() {
        return true;
    }
    
    /**
     * INTERNAL: 
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        if (mapping.isAbstractDirectMapping()) {
            AbstractDirectMapping directMapping = ((AbstractDirectMapping)mapping); 
            directMapping.setFieldType(Types.STRUCT);
            directMapping.setConverter(null);
            directMapping.setConverterClassName(null);
        } else if (!(mapping.isDirectCollectionMapping() || mapping.isDirectMapMapping())){
            throw ValidationException.invalidMappingForStructConverter(getName(), mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConverter(String converter) {
        m_converter = converter;
    }
}

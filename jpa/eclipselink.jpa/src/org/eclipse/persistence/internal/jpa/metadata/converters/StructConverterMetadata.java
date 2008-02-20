/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.lang.reflect.AnnotatedElement;
import java.sql.Types;

import org.eclipse.persistence.annotations.StructConverter;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;

import org.eclipse.persistence.jpa.config.StructConverterType;

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
    private String m_converter;
    
    /**
     * INTERNAL:
     */
    public StructConverterMetadata() {
    	setLoadedFromXML();
    }
    
    /**
     * INTERNAL:
     */
    public StructConverterMetadata(StructConverter converter, AnnotatedElement annotatedElement) {
    	setLoadedFromAnnotation();
    	setLocation(annotatedElement);
    	
    	setName(converter.name());
        setConverter(converter.converter());
    }
    
    /**
     * INTERNAL:
     */
    public boolean equals(Object objectToCompare) {
    	if (objectToCompare instanceof StructConverterMetadata) {
    		StructConverterMetadata structConverter = (StructConverterMetadata) objectToCompare;
    		
    		if (! MetadataHelper.valuesMatch(getName(), structConverter.getName())) {
    			return false;
    		}
    		
    		return MetadataHelper.valuesMatch(m_converter, structConverter.getConverter());
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
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        if (mapping.isAbstractDirectMapping()){
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

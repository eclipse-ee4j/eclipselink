/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.lang.annotation.Annotation;

import javax.persistence.TemporalType;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * INTERNAL:
 * Abstract converter class that parents both the JPA and Eclipselink 
 * converters.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.0
 */
public class TemporalMetadata extends MetadataConverter {
    private Enum m_temporal;
    
    /**
     * INTERNAL:
     */
    public TemporalMetadata() {
        super("<temporal>");
    }
    
    /**
     * INTERNAL:
     */
    public TemporalMetadata(Annotation temporal, MetadataAccessibleObject accessibleObject) {
        super(temporal, accessibleObject);
        
        m_temporal = (Enum) MetadataHelper.invokeMethod("value", temporal);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getTemporal() {
        return m_temporal;
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid temporal type and must be
     * marked temporal.
     */
    public static boolean isValidTemporalType(Class cls) {
        return (cls.equals(java.util.Date.class) ||
                cls.equals(java.util.Calendar.class) ||
                cls.equals(java.util.GregorianCalendar.class));
    }
    
    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, Class referenceClass, boolean isForMapKey) {
        if (isValidTemporalType(referenceClass)) {
            // Set a TypeConversionConverter on the mapping.
            if (m_temporal.name().equals(TemporalType.DATE.name())) {
                setFieldClassification(mapping, java.sql.Date.class, isForMapKey);
            } else if(m_temporal.name().equals(TemporalType.TIME.name())) {
                setFieldClassification(mapping,java.sql.Time.class, isForMapKey);
            } else {
                // Through annotation and XML validation, it must be 
                // TemporalType.TIMESTAMP and can't be anything else.
                setFieldClassification(mapping, java.sql.Timestamp.class, isForMapKey);
            }
            
            setConverter(mapping, new TypeConversionConverter(mapping), isForMapKey);
        } else {
            throw ValidationException.invalidTypeForTemporalAttribute(accessor.getAttributeName(), referenceClass, accessor.getJavaClass());
        }   
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTemporal(Enum temporalType) {
        m_temporal = temporalType;
    }
}

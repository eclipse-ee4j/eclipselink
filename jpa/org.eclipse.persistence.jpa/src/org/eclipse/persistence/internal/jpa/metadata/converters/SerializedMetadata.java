/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel 
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import java.io.Serializable;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * INTERNAL:
 * Abstract converter class that parents both the JPA and Eclipselink 
 * converters.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class SerializedMetadata extends MetadataConverter {
    /**
     * INTERNAL:
     */
    public SerializedMetadata() {}
    
    /**
     * Used for defaulting.
     */
    public SerializedMetadata(MetadataAccessibleObject accessibleObject) {
        super(accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    public SerializedMetadata(MetadataAnnotation converter, MetadataAccessibleObject accessibleObject) {
        super(converter, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey) {
        if (accessor.getReferenceClass().extendsInterface(Serializable.class)
                || accessor.getReferenceClass().isArray()
                || accessor.getReferenceClass().isInterface()) {
            setConverter(mapping, new SerializedObjectConverter(mapping), isForMapKey);
        } else {
            // 266912: relax validation for MappedSuperclass descriptors
            if (!accessor.getClassAccessor().isMappedSuperclass()) {
                throw ValidationException.invalidTypeForSerializedAttribute(mapping.getAttributeName(), accessor.getReferenceClass(), accessor.getJavaClass());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    public void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, MetadataClass classification, boolean isForMapKey) {
        process(mapping, accessor, referenceClass, isForMapKey);
        
        // Set the specific classification provided.
        if (classification != null) {
            setFieldClassification(mapping, getJavaClass(classification), isForMapKey);
        }
    }
}

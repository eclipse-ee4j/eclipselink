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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
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
public abstract class MetadataConverter extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    /**
     * INTERNAL:
     * Used for defaulting case.
     */
    protected MetadataConverter() {}
    
    /**
     * INTERNAL:
     */
    public MetadataConverter(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * Used for Annotation loading.
     */
    public MetadataConverter(MetadataAnnotation converter, MetadataAccessibleObject accessibleObject) {
        super(converter, accessibleObject);
    }
    
    /**
     * Used for defaulting.
     */
    public MetadataConverter(MetadataAccessibleObject accessibleObject) {
        super(accessibleObject);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return objectToCompare instanceof MetadataConverter;
    }
    
    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    public abstract void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey);
    
    /**
     * INTERNAL:
     */
    protected void setConverter(DatabaseMapping mapping, Converter converter, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverter(converter);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverter(converter);
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Be nice to support converters on AggregateCollections keys.
            // For now they are silently ignored.
        } else if (mapping.isDirectToFieldMapping()) {
            ((DirectToFieldMapping) mapping).setConverter(converter);
        }
    }

    /**
     * INTERNAL:
     */
    protected void setConverterClassName(DatabaseMapping mapping, String converterClassName, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverterClassName(converterClassName);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverterClassName(converterClassName);
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Be nice to support converters on AggregateCollections keys.
            // For now they are silently ignored.
        } else if (mapping.isDirectToFieldMapping()) {
            ((DirectToFieldMapping) mapping).setConverterClassName(converterClassName);    
        }
    }

    /**
     * INTERNAL:
     */
    protected void setFieldClassification(DatabaseMapping mapping, Class classification, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setDirectKeyFieldClassification(classification);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setDirectFieldClassification(classification);
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Future and AggregateCollections should only be able to get 
            // in here for a key converter
        } else if (mapping.isDirectToFieldMapping()) {
            ((DirectToFieldMapping) mapping).setFieldClassificationClassName(classification.getName());
        }
    }
}

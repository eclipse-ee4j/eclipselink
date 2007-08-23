/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import org.eclipse.persistence.annotations.BasicMap;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * A basic collection accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class BasicMapAccessor extends BasicCollectionAccessor {    
    protected BasicMap m_basicMap;
    protected String m_keyConverter;
    protected String m_valueConverter;
    protected MetadataColumn m_keyColumn;
    protected boolean m_keyContextProcessing;
    
    /**
     * INTERNAL:
     */
    public BasicMapAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        m_basicMap = getAnnotation(BasicMap.class);
        m_keyColumn = new MetadataColumn(m_basicMap.keyColumn(), this);
        m_keyConverter = m_basicMap.keyConverter().value();
        m_valueColumn = new MetadataColumn(m_basicMap.valueColumn(), this);
        m_valueConverter = m_basicMap.valueConverter().value();
    }
   
    /**
     * INTERNAL: (OVERRIDE)
     */
    protected MetadataColumn getColumn(String loggingCtx) {
        return (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) ? m_valueColumn : m_keyColumn;
    }
    
    /**
     * INTERNAL: (Overridden in XMLBasicMapAccessor)
     */
    public String getFetchType() {
        return m_basicMap.fetch().name();
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor)
     * 
     * Return the reference class for this accessor.
     */
    public Class getReferenceClass() {
        if (m_keyContextProcessing) {
            return getMapKeyClass();            
        } else {
            return getReferenceClassFromGeneric();
        }
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor)
     * 
	 * A BasicMap always has a @Convert specified. They are defaulted within
     * the @BasicMap. This will be used to log warning messages when ignoring
     * JPA converters for lob, temporal, enumerated and serialized.
     */
	protected boolean hasConvert() {
		return true;
    }
    
    /**
     * INTERNAL:
     * 
     * Return true if this accessor represents a basic map mapping.
     */
    public boolean isBasicMap() {
        return true;
    }
    
    /**
     * INTERNAL:
     * The processing of JPA converters for a basic map has been disabled, since 
     * we will not know which part of the map (key or value) to apply the JPA 
     * converter. See isLob, isTemporal and isEnumerated calls for log warning 
     * details.
     */
    public void process() {
        // Initialize our mapping.
        DirectMapMapping mapping = new DirectMapMapping();
        
        // Process common direct collection metadata. This must be done before
        // any field processing since field processing requires that the
        // collection table be processed before hand.
        process(mapping);
        
        // Process the fetch type
        if (usesIndirection()) {
            mapping.useTransparentMap();
        } else {
            mapping.dontUseIndirection();
            mapping.useMapClass(java.util.Hashtable.class);
        }
        
        // Process the key column (we must process this field before the call
        // to processConverter, since it may set a field classification)
        mapping.setDirectKeyField(getDatabaseField(m_logger.MAP_KEY_COLUMN));
        
        // Process a converter for the key column of this mapping.
        processMappingConverter(mapping, m_keyConverter);
        
        // Process the value column (we must process this field before the call
        // to processConverter, since it may set a field classification)
        mapping.setDirectField(getDatabaseField(m_logger.VALUE_COLUMN));
        
        // Process a converter for value column of this mapping.
        processMappingConverter(mapping, m_valueConverter);
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     * 
     * Process a @Convert annotation or convert element to apply to specfiied 
     * TopLink converter (@Converter, @TypeConverter, @ObjectTypeConverter) to
     * the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a @Convert specified.
     */
    public void processConvert() {
        DatabaseMapping mapping = m_descriptor.getMappingForAttributeName(getAttributeName());
        
        m_keyContextProcessing = true;
        processConvert(mapping, m_keyConverter);
        
        m_keyContextProcessing = false;
        processConvert(mapping, m_valueConverter);
    }
    
    /**
     * INTERNAL:
     */
    public void setConverter(DatabaseMapping mapping, Converter converter) {
        if (m_keyContextProcessing) {
            ((DirectMapMapping) mapping).setKeyConverter(converter);
        } else {
            ((DirectMapMapping) mapping).setValueConverter(converter);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setConverterClassName(DatabaseMapping mapping, String converterClassName) {
        if (m_keyContextProcessing) {
            ((DirectMapMapping) mapping).setKeyConverterClassName(converterClassName);
        } else {
            ((DirectMapMapping) mapping).setValueConverterClassName(converterClassName);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        if (m_keyContextProcessing) {
            ((DirectMapMapping) mapping).setDirectFieldClassification(classification);
        } else {
            ((DirectMapMapping) mapping).setDirectFieldClassification(classification);
        }
    }
}

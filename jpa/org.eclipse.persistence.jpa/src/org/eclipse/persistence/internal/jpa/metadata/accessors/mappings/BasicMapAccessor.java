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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * INTERNAL:
 * A basic collection accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class BasicMapAccessor extends BasicCollectionAccessor {    
    private ColumnMetadata m_keyColumn;
    private String m_keyConverter;
    private String m_valueConverter;
    private boolean m_keyContextProcessing;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public BasicMapAccessor() {
        super("<basic-map>");
    }
    
    /**
     * INTERNAL:
     */
    public BasicMapAccessor(Annotation basicMap, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(basicMap, accessibleObject, classAccessor);

        m_keyColumn = new ColumnMetadata((Annotation) MetadataHelper.invokeMethod("keyColumn", basicMap), accessibleObject, getAttributeName());

        Annotation keyConvert = (Annotation) MetadataHelper.invokeMethod("keyConverter", basicMap);
        m_keyConverter = (String) MetadataHelper.invokeMethod("value", keyConvert);

        Annotation valueConvert = (Annotation) MetadataHelper.invokeMethod("valueConverter", basicMap);
        m_valueConverter = (String)MetadataHelper.invokeMethod("value", valueConvert);
        
        setValueColumn(new ColumnMetadata((Annotation) MetadataHelper.invokeMethod("valueColumn", basicMap), accessibleObject, getAttributeName()));
        setFetch((Enum) MetadataHelper.invokeMethod("fetch", basicMap));
    }
   
    /**
     * INTERNAL:
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            return super.getColumn(loggingCtx);
        } else {
            return (m_keyColumn == null) ? new ColumnMetadata(getAccessibleObject(), getAttributeName()) : m_keyColumn; 
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected String getDefaultCollectionTableName() {
        if (m_keyColumn != null && m_keyColumn.getTable() != null && ! m_keyColumn.getTable().equals("")) {
            return m_keyColumn.getTable();
        } else {
            return super.getDefaultCollectionTableName(); 
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getKeyColumn() {
        return m_keyColumn;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getKeyConverter() {
        return m_keyConverter;
    }
    
    /**
     * INTERNAL:
     * Return the reference class for this accessor.
     */
    @Override
    public Class getReferenceClass() {
        if (m_keyContextProcessing) {
            return getAccessibleObject().getMapKeyClass(getDescriptor());            
        } else {
            return getReferenceClassFromGeneric();
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValueConverter() {
        return m_valueConverter;
    }
 
    /**
     * INTERNAL:
     * A BasicMap always has a Convert specified. They are defaulted within
     * the BasicMap annotation. This will be used to log warning messages when 
     * ignoring JPA converters for lob, temporal, enumerated and serialized.
     */
    @Override
    protected boolean hasConvert() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize single ORMetadata objects.
        initXMLObject(m_keyColumn, accessibleObject);
        
        // Make sure the attribute name is set on the key column if one is
        // set through XML.
        if (m_keyColumn != null) {
            m_keyColumn.setAttributeName(getAttributeName());
        }
    }
    
    /**
     * INTERNAL:
     * Returns true if the given class is a valid basic map type.
     */ 
    protected boolean isValidBasicMapType(Class cls) {
        return cls.equals(Map.class);
    }
    
    /**
     * INTERNAL:
     * The processing of JPA converters for a basic map has been disabled, since 
     * we will not know which part of the map (key or value) to apply the JPA 
     * converter. See isLob, isTemporal and isEnumerated calls for log warning 
     * details.
     */
    @Override
    public void process() {
        if (isValidBasicMapType(getRawClass())) {
            // Initialize our mapping.
            DirectMapMapping mapping = new DirectMapMapping();
            
            // Process common direct collection metadata. This must be done 
            // before any field processing since field processing requires that 
            // the collection table be processed before hand.
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
            mapping.setDirectKeyField(getDatabaseField(mapping.getReferenceTable(), MetadataLogger.MAP_KEY_COLUMN));
            
            // Process a converter for the key column of this mapping.
            processMappingConverter(mapping, m_keyConverter);
            
            // Process the value column (we must process this field before the call
            // to processConverter, since it may set a field classification)
            mapping.setDirectField(getDatabaseField(mapping.getReferenceTable(), MetadataLogger.VALUE_COLUMN));
            
            // Process a converter for value column of this mapping.
            processMappingConverter(mapping, m_valueConverter);    
            
            // process properties
            processProperties(mapping);
        } else {
            throw ValidationException.invalidTypeForBasicMapAttribute(getAttributeName(), getRawClass(), getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * Process a convert value to apply a specified EclipseLink converter 
     * (Converter, TypeConverter, ObjectTypeConverter) to the given mapping.
     * 
     * This method is called in second stage processing and should only be
     * called on accessors that have a @Convert specified.
     */
    @Override
    public void processConvert() {
        DatabaseMapping mapping = getDescriptor().getMappingForAttributeName(getAttributeName());
        
        m_keyContextProcessing = true;
        processConvert(mapping, m_keyConverter);
        
        m_keyContextProcessing = false;
        processConvert(mapping, m_valueConverter);
    }
    
    /**
     * INTERNAL:
     */
    @Override
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
    @Override
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
    @Override
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        if (m_keyContextProcessing) {
            ((DirectMapMapping) mapping).setDirectKeyFieldClassification(classification);
        } else {
            super.setFieldClassification(mapping, classification);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setKeyColumn(ColumnMetadata keyColumn) {
        m_keyColumn = keyColumn;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setKeyConverter(String keyConverter) {
        m_keyConverter = keyConverter;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValueConverter(String valueConverter) {
        m_valueConverter = valueConverter;
    }
}

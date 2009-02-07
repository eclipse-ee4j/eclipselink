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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

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
    @Override
    public String getKeyConverter() {
        return m_keyConverter;
    }
    
    /**
     * INTERNAL:
     * Return the reference class for this accessor.
     */
    @Override
    public Class getReferenceClass() {
        if (isKeyContextProcessing()) {
            return getAccessibleObject().getMapKeyClass(getDescriptor());            
        } else {
            return getReferenceClassFromGeneric();
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
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
        if (isValidDirectMapType()) {
            processDirectMapMapping();
        } else {
            throw ValidationException.invalidTypeForBasicMapAttribute(getAttributeName(), getRawClass(), getJavaClass());
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

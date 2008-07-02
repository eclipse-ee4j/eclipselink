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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;

import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ReturnInsert;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * INTERNAL:
 * A relational accessor. A Basic annotation may or may not be present on the
 * accessible object.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class BasicAccessor extends DirectAccessor {
    private Boolean m_mutable;
    private ColumnMetadata m_column;
    private DatabaseField m_field;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public BasicAccessor() {
        super("<basic>");
    }
    
    /**
     * INTERNAL:
     */
    public BasicAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public BasicAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        // Set the basic metadata if one is present.
        Annotation basic = getAnnotation(Basic.class);
        if (basic != null) {
            setFetch((Enum) MetadataHelper.invokeMethod("fetch", basic));
            setOptional((Boolean) MetadataHelper.invokeMethod("optional", basic));
        }
        
        // Set the column metadata if one if present.
        m_column = new ColumnMetadata(getAnnotation(Column.class), accessibleObject, getAttributeName());
        
        // Set the mutable value if one is present.
        Annotation mutable = getAnnotation(Mutable.class);
        if (mutable != null) {
            m_mutable = (Boolean) MetadataHelper.invokeMethod("value", mutable);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }
    
    /**
     * INTERNAL:
     * Return the column from xml if there is one, otherwise look for an
     * annotation.
     */
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (m_column == null) {
            return new ColumnMetadata(getAccessibleObject(), getAttributeName());
        } else {
            return m_column;
        }
    }

    /**
     * INTERNAL:
     * Process column metadata details and resolve any generic specifications.
     */
    @Override
    protected DatabaseField getDatabaseField(DatabaseTable defaultTable, String loggingCtx) {
        // Get the actual database field and apply any defaults.
        DatabaseField field = super.getDatabaseField(defaultTable, loggingCtx);
        
        // To correctly resolve the generics at runtime, we need to set the 
        // field type.
        if (getAccessibleObject().isGenericType()) {
            field.setType(getReferenceClass());
        }
                    
        return field;
    }
    
    /**
     * INTERNAL:
     */
    public FetchType getDefaultFetchType() {
        return FetchType.EAGER; 
    }
    
    /**
     * INTERNAL:
     */
    protected DatabaseField getField() {
        return m_field;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getMutable() {
        return m_mutable;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);

        // Default a column if necessary.
        if (m_column == null) {
            m_column = new ColumnMetadata(accessibleObject, getAttributeName());
        } else {
            // Make sure the attribute name is set on the column.
            m_column.setAttributeName(getAttributeName());

            // Initialize single objects.
            initXMLObject(m_column, accessibleObject);
        }
    }
    
    /**
     * INTERNAL:
     * Method to return whether a class is a collection or not. 
     */
    protected boolean isCollectionClass(Class cls) {
        return Collection.class.isAssignableFrom(cls);
    }
    
    /**
     * INTERNAL:
     * Method to return whether a class is a map or not. 
     */
    protected boolean isMapClass(Class cls) {
        return Map.class.isAssignableFrom(cls);
    }
    
    /**
     * INTERNAL:
     * Process a basic accessor.
     */
    public void process() {
        // Process a DirectToFieldMapping, that is a Basic that could
        // be used in conjunction with a Lob, Temporal, Enumerated
        // or inferred to be used with a serialized mapping.
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        
        // Process the @Column or column element if there is one.
        m_field = getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN);
        
        // To resolve any generic types we need to set the attribute
        // classification on the mapping to ensure we do the right 
        // conversions.
        if (getAccessibleObject().isGenericType()) {
            mapping.setAttributeClassification(getReferenceClass());
        }
        
        mapping.setField(m_field);
        mapping.setIsReadOnly(m_field.isReadOnly());
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        mapping.setIsLazy(usesIndirection());

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);

        // Process a converter for this mapping. We will look for a convert
        // value first. If none is found then we'll look for a JPA converter, 
        // that is, Enumerated, Lob and Temporal. With everything falling into 
        // a serialized mapping if no converter whatsoever is found.
        processMappingConverter(mapping);

        // Process a mutable setting.
        if (m_mutable != null) {
            mapping.setIsMutable(m_mutable.booleanValue());
        }

        // Process a @ReturnInsert
        processReturnInsert(m_field);

        // Process a @ReturnUpdate.
        processReturnUpdate(m_field);
        
        // process properties
        processProperties(mapping);
        
        // Add the mapping to the descriptor.
        getDescriptor().addMapping(mapping);
    }

    /**
     * INTERNAL:
     * Process an Enumerated annotation. The method may still be called if no 
     * Enumerated annotation has been specified but the accessor's reference 
     * class is a valid enumerated type.
     */
    @Override
    protected void processEnumerated(DatabaseMapping mapping) {
        // If the raw class is a collection or map (with generics or not), we 
        // don't want to put a TypeConversionConverter on the mapping. Instead, 
        // we will want a serialized converter. For example, we could have 
        // an EnumSet<Enum> relation type.
        if (isCollectionClass(getReferenceClass()) || isMapClass(getReferenceClass())) {
            processSerialized(mapping);
        } else {
            super.processEnumerated(mapping);
        }
    }

    /**
     * INTERNAL:
     * Process a Lob metadata. The lob must be specified to process and 
     * create a lob type mapping.
     */
    @Override
    protected void processLob(DatabaseMapping mapping) {
        // If the raw class is a collection or map (with generics or not), we 
        // don't want to put a TypeConversionConverter on the mapping. Instead, 
        // we will want a serialized converter.
        if (isCollectionClass(getReferenceClass()) || isMapClass(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Blob.class);
            processSerialized(mapping);
        } else {
            super.processLob(mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Process a ReturnInsert annotation.
     */
    protected void processReturnInsert(DatabaseField field) {
        Object returnInsert = getAnnotation(ReturnInsert.class);

        if (returnInsert != null) {
            // Process return only.
            processReturnInsert(field, (Boolean) MetadataHelper.invokeMethod("returnOnly", returnInsert)); 
        }
    }

    /**
     * INTERNAL:
     * Process a return insert setting.
     */
    protected void processReturnInsert(DatabaseField field, boolean returnOnly) {
        if (returnOnly) {
            getDescriptor().addFieldForInsertReturnOnly(field);
        } else {
            getDescriptor().addFieldForInsert(field);
        }
    }

    /**
     * INTERNAL:
     * Process a return update setting.
     */
    protected void processReturnUpdate(DatabaseField field) {
        if (hasReturnUpdate()) {
            getDescriptor().addFieldForUpdate(field);
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
    
    /**
     * INTERNAL:
     */
    public void setConverter(DatabaseMapping mapping, Converter converter) {
        ((DirectToFieldMapping)mapping).setConverter(converter);
    }

    /**
     * INTERNAL:
     */
    public void setConverterClassName(DatabaseMapping mapping, String converterClassName) {
        ((DirectToFieldMapping)mapping).setConverterClassName(converterClassName);
    }

    /**
     * INTERNAL:
     */
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        ((DirectToFieldMapping)mapping).setFieldClassification(classification);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMutable(Boolean mutable) {
        m_mutable = mutable;
    }
}

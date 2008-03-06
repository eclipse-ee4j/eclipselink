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

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
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
     */
    public BasicAccessor() {}
    
    /**
     * INTERNAL:
     */
    public BasicAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        // Set the basic metadata if one is present.
        Annotation basic = getAnnotation(Basic.class);
        if (basic != null) {
            setFetch((Enum) MetadataHelper.invokeMethod("fetch", basic));
            setOptional((Boolean) MetadataHelper.invokeMethod("optional", basic));
        }
        
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
            return new ColumnMetadata(getAnnotation(Column.class), getAttributeName());
    	} else {
    		return m_column;
    	}
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
     * INTERNAL: (Override from MetadataAccessor)
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
    	super.init(accessibleObject, accessor);
    	
    	// Make sure the attribute name is set on the column if one is set in
    	// XML.
    	if (m_column != null) {
    		m_column.setAttributeName(getAttributeName());
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
        // Process the @Column or column element if there is one.
        m_field = getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN);

        // Process a @ReturnInsert
        processReturnInsert(m_field);

        // Process a @ReturnUpdate.
        processReturnUpdate(m_field);

        if (getDescriptor().hasMappingForAttributeName(getAttributeName())) {
            // Ignore the mapping if one already exists for it.
            getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPING, this);
        } else {
            // Process a DirectToFieldMapping, that is a Basic that could
            // be used in conjunction with a Lob, Temporal, Enumerated
            // or inferred to be used with a serialized mapping.
            processDirectToFieldMapping(m_field);
        }
    }

    /**
     * INTERNAL:
     * Process a Serialized or Basic into a DirectToFieldMapping. If neither 
     * is found a DirectToFieldMapping is created regardless.
     */
    protected void processDirectToFieldMapping(DatabaseField field) {
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setField(field);
        mapping.setIsReadOnly(field.isReadOnly());
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

        // Add the mapping to the descriptor.
        getDescriptor().addMapping(mapping);
    }

    /**
     * INTERNAL: (Override from DirectAccessor)
     * Process an @Enumerated. The method may still be called if no @Enumerated
     * has been specified but the accessor's reference class is a valid 
     * enumerated type.
     */
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
     * INTERNAL: (Override from DirectAccessor)
     * Process a Lob metadata. The lob must be specified to process and 
     * create a lob type mapping.
     */
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
     * INTERNAL: (Override from MetadataAccessor)
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
     * INTERNAL: (Override from MetadataAccessor)
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

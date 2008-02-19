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

import javax.persistence.FetchType;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * A basic collection accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class BasicCollectionAccessor extends DirectAccessor {
	private boolean m_privateOwned;
	private ColumnMetadata m_valueColumn;
    private CollectionTableMetadata m_collectionTable;
    private JoinFetchType m_joinFetch;
    
    /**
     * INTERNAL:
     */
    public BasicCollectionAccessor() {}
    
    /**
     * INTERNAL:
     */
    public BasicCollectionAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        BasicCollection basicCollection = getAnnotation(BasicCollection.class);
        
        // Must check, BasicMapAccessor calls this constructor ...
        if (basicCollection != null) {
            m_valueColumn = new ColumnMetadata(basicCollection.valueColumn(), this);
            
            setFetch(basicCollection.fetch());
        }
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    protected CollectionTableMetadata getCollectionTable() {
    	return m_collectionTable;
    }
    
    /**
     * INTERNAL: (Override from BasicAccessor, Overridden in BasicMapAccessor)
     * Method ignores logging context. Can't be anything but the value 
     * column for a BasicCollection annotation. Used with the BasicMap 
     * annotation however.
     */
    protected ColumnMetadata getColumn(String loggingCtx) {
    	return (m_valueColumn == null) ? new ColumnMetadata(this) : m_valueColumn;  
    }
    
    /**
     * INTERNAL: (Overridden in BasicMapAccessor)
     */
    protected String getDefaultCollectionTableName() {
    	if (m_valueColumn != null && m_valueColumn.getTable() != null && !m_valueColumn.getTable().equals("")) {
    		return m_valueColumn.getTable();
    	} else {
    		return getUpperCaseShortJavaClassName() + "_" + getUpperCaseAttributeName(); 
    	}
    }
    
    /**
     * INTERNAL:
     */
    public FetchType getDefaultFetchType() {
    	return FetchType.LAZY; 
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public JoinFetchType getJoinFetch() {
    	return m_joinFetch;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getPrivateOwned() {
    	return null;
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor)
     * Return the reference class for this accessor. It will try to extract
     * a reference class from a generic specification. If no generics are used,
     * then it will return void.class. This avoids NPE's when processing
     * JPA converters that can default (Enumerated and Temporal) based on the
     * reference class.
     */
    public Class getReferenceClass() {
        Class cls = getReferenceClassFromGeneric();
        return (cls == null) ? void.class : cls;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getValueColumn() {
        return m_valueColumn;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasPrivateOwned() {
    	return m_privateOwned || isAnnotationPresent(PrivateOwned.class);
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor, Overridden in BasicMapAccessor)
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
    	super.init(accessibleObject, accessor);
    	
    	// Make sure the attribute name is set on the value column if one is
    	// set through XML.
    	if (m_valueColumn != null) {
    		m_valueColumn.setAttributeName(getAttributeName());
    	}
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    public boolean isBasicCollection() {
        return true;
    }
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public boolean isPrivateOwned() {
		return m_privateOwned;
	}
	
    /**
     * INTERNAL: (Overridden in BasicMapAccessor)
     */
    public void process() {
        if (MetadataHelper.isValidBasicCollectionType(getRawClass())) {
            // Initialize our mapping.
            DirectCollectionMapping mapping = new DirectCollectionMapping();
            
            // Process common direct collection metadata. This must be done 
            // before any field processing since field processing requires that 
            // the collection table be processed before hand.
            process(mapping);
            
            // Process the fetch type and set the correct indirection on the 
            // mapping.
            setIndirectionPolicy(mapping, null, usesIndirection());
            
            // Process the value column (we must process this field before the 
            // call to processConverter, since it may set a field classification)
            mapping.setDirectField(getDatabaseField(mapping.getReferenceTable(), MetadataLogger.VALUE_COLUMN));
            
            // Process a converter for this mapping. We will look for a Convert
            // value. If none is found then we'll look for a JPA converter, that 
            // is, Enumerated, Lob and Temporal. With everything falling into 
            // a serialized mapping if no converter whatsoever is found.
            processMappingConverter(mapping);        	
        } else {
        	throw ValidationException.invalidTypeForBasicCollectionAttribute(getAttributeName(), getRawClass(), getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void process(DirectCollectionMapping mapping) {
        // Set the attribute name.
        mapping.setAttributeName(getAttributeName());
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);
        
        // Process private owned.
        mapping.setIsPrivateOwned(hasPrivateOwned());
        
        // Process join fetch type.
        mapping.setJoinFetch(getMappingJoinFetchType(m_joinFetch));
        
        // Process the collection table.
        processCollectionTable(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        // Add the mapping to the descriptor.
        getDescriptor().addMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Process a MetadataCollectionTable.
     */
    protected void processCollectionTable(DirectCollectionMapping mapping) {
    	CollectionTableMetadata collectionTable;
    	
    	if (m_collectionTable == null) {
    		CollectionTable table = getAnnotation(CollectionTable.class);
    		collectionTable = new CollectionTableMetadata(table, getAnnotatedElementName());
    	} else {
    		collectionTable = m_collectionTable;
    	}
        
        // Process any table defaults and log warning messages.
        processTable(collectionTable, getDefaultCollectionTableName());
        
        // Set the reference table on the mapping.
        mapping.setReferenceTable(collectionTable.getDatabaseTable());
        
        // Add all the primaryKeyJoinColumns (reference key fields) to the 
        // mapping. Primary key join column validation is performed in the
        // processPrimaryKeyJoinColumns call.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : processPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsMetadata(collectionTable.getPrimaryKeyJoinColumns()))) {
            // The default name is the primary key of the owning entity.
            DatabaseField pkField = primaryKeyJoinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, getDescriptor().getPrimaryKeyFieldName(), MetadataLogger.PK_COLUMN));
            pkField.setTable(getDescriptor().getPrimaryTable());
            
            // The default name is the primary key of the owning entity.
            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, getDescriptor().getPrimaryKeyFieldName(), MetadataLogger.FK_COLUMN));
            fkField.setTable(collectionTable.getDatabaseTable());
            
            // Add the reference key field for the direct collection mapping.
            mapping.addReferenceKeyField(fkField, pkField);
        }
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    protected void setCollectionTable(CollectionTableMetadata collectionTable) {
    	m_collectionTable = collectionTable;
    }
    
    /**
     * INTERNAL: (Overridden in BasicMapAccessor)
     */
    public void setConverter(DatabaseMapping mapping, Converter converter) {
        ((DirectCollectionMapping) mapping).setValueConverter(converter);
    }
    
    /**
     * INTERNAL:
     */
    public void setConverterClassName(DatabaseMapping mapping, String converterClassName) {
        ((DirectCollectionMapping) mapping).setValueConverterClassName(converterClassName);
    }
    
    /**
     * INTERNAL: (Overridden in BasicMapAccessor)
     */
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        ((DirectCollectionMapping) mapping).setDirectFieldClassification(classification);
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setJoinFetch(JoinFetchType joinFetch) {
    	m_joinFetch = joinFetch;
    }
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setPrivateOwned(String ignore) {
		m_privateOwned = true;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValueColumn(ColumnMetadata valueColumn) {
        m_valueColumn = valueColumn;
    }
}

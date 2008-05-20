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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.FetchType;

import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * INTERNAL:
 * A basic collection accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class BasicCollectionAccessor extends DirectAccessor {
    private boolean m_privateOwned;
    private ColumnMetadata m_valueColumn;
    private CollectionTableMetadata m_collectionTable;
    private Enum m_joinFetch;
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public BasicCollectionAccessor() {
        super("<basic-collection>");
    }
    
    /**
     * INTERNAL:
     */
    protected BasicCollectionAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public BasicCollectionAccessor(Annotation basicCollection, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(basicCollection, accessibleObject, classAccessor);

        // Must check, BasicMapAccessor calls this constructor ...
        if (basicCollection != null) {
            m_valueColumn = new ColumnMetadata((Annotation) MetadataHelper.invokeMethod("valueColumn", basicCollection), accessibleObject, getAttributeName());
            setFetch((Enum) MetadataHelper.invokeMethod("fetch", basicCollection));
        }
        
        // Set the collection table if one is present.
        if (isAnnotationPresent(CollectionTable.class)) {
            m_collectionTable = new CollectionTableMetadata(getAnnotation(CollectionTable.class), accessibleObject);
        }
        
        // Set the join fetch if one is present.
        Annotation joinFetch = getAnnotation(JoinFetch.class);            
        if (joinFetch != null) {
            m_joinFetch = (Enum) MetadataHelper.invokeMethod("value", joinFetch);
        }
        
        // Set the private owned if one is present.
        m_privateOwned = isAnnotationPresent(PrivateOwned.class);
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    protected CollectionTableMetadata getCollectionTable() {
        return m_collectionTable;
    }
    
    /**
     * INTERNAL:
     * Method ignores logging context. Can't be anything but the value 
     * column for a BasicCollection annotation. Used with the BasicMap 
     * annotation however.
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        return (m_valueColumn == null) ? new ColumnMetadata(getAccessibleObject(), getAttributeName()) : m_valueColumn;  
    }
    
    /**
     * INTERNAL:
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
    public Enum getJoinFetch() {
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
     * INTERNAL:
     * Return the reference class for this accessor. It will try to extract
     * a reference class from a generic specification. If no generics are used,
     * then it will return void.class. This avoids NPE's when processing
     * JPA converters that can default (Enumerated and Temporal) based on the
     * reference class.
     */
    @Override
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
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
    
        // Initialize single ORMetadata objects.
        initXMLObject(m_valueColumn, accessibleObject);
        
        // Initialize lists of ORMetadata objects.
        initXMLObject(m_collectionTable, accessibleObject);
        
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
    @Override
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
     * INTERNAL:
     * Returns true if the given class is a valid basic collection type.
     */ 
    protected boolean isValidBasicCollectionType() {
        Class rawClass = getRawClass();
        
        return rawClass.equals(Collection.class) || 
               rawClass.equals(Set.class) ||
               rawClass.equals(List.class);
    }
    
    /**
     * INTERNAL:
     */
    public void process() {
        if (isValidBasicCollectionType()) {
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
            
            // Process a converter for this mapping. We will look for a convert
            // value. If none is found then we'll look for a JPA converter, that 
            // is, Enumerated, Lob and Temporal. With everything falling into 
            // a serialized mapping if no converter whatsoever is found.
            processMappingConverter(mapping);
            
            // process properties
            processProperties(mapping);
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
        mapping.setIsPrivateOwned(m_privateOwned);
        
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
        // Check that we loaded a collection table otherwise default one.        
        if (m_collectionTable == null) {
            // TODO: Log a defaulting message.
            m_collectionTable = new CollectionTableMetadata(null, getAccessibleObject());
        }
        
        // Process any table defaults and log warning messages.
        processTable(m_collectionTable, getDefaultCollectionTableName());
        
        // Set the reference table on the mapping.
        mapping.setReferenceTable(m_collectionTable.getDatabaseTable());
        
        // Add all the primaryKeyJoinColumns (reference key fields) to the 
        // mapping. Primary key join column validation is performed in the
        // processPrimaryKeyJoinColumns call.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : processPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsMetadata(m_collectionTable.getPrimaryKeyJoinColumns()))) {
            // The default name is the primary key of the owning entity.
            DatabaseField pkField = primaryKeyJoinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, getDescriptor().getPrimaryKeyFieldName(), MetadataLogger.PK_COLUMN));
            pkField.setTable(getDescriptor().getPrimaryTable());
            
            // The default name is the primary key of the owning entity.
            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, getDescriptor().getPrimaryKeyFieldName(), MetadataLogger.FK_COLUMN));
            fkField.setTable(m_collectionTable.getDatabaseTable());
            
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
     * INTERNAL:
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
     * INTERNAL:
     */
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        ((DirectCollectionMapping) mapping).setDirectFieldClassification(classification);
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setJoinFetch(Enum joinFetch) {
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

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
 *     James Sutherland - initial impl
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     06/18/2010-2.2 Guy Pelletier 
 *       - 300458: EclispeLink should throw a more specific exception than NPE
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

/**
 * Object to hold onto primary key metadata.
 * 
 * @see PrimaryKey
 * @author James Sutherland
 * @since EclipseLink 1.1
 */
public class PrimaryKeyMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private String m_validation;
    private String m_cacheKeyType;
    private List<ColumnMetadata> m_columns = new ArrayList<ColumnMetadata>();

    /**
     * INTERNAL:
     */
    public PrimaryKeyMetadata() {
        super("<primary-key>");
    }

    /**
     * INTERNAL:
     */
    public PrimaryKeyMetadata(MetadataAnnotation primaryKey, MetadataAccessibleObject accessibleObject) {
        super(primaryKey, accessibleObject);
        
        m_validation = (String) primaryKey.getAttribute("validation");
        
        for (Object selectedColumn : (Object[]) primaryKey.getAttributeArray("columns")) {
            m_columns.add(new ColumnMetadata((MetadataAnnotation)selectedColumn, accessibleObject));
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PrimaryKeyMetadata) {
            PrimaryKeyMetadata primaryKey = (PrimaryKeyMetadata) objectToCompare;
            
            if (! valuesMatch(m_validation, primaryKey.getValidation())) {
                return false;
            }
            
            if (! valuesMatch(m_cacheKeyType, primaryKey.getCacheKeyType())) {
                return false;
            }
            
            return valuesMatch(m_columns, primaryKey.getColumns());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCacheKeyType() {
        return m_cacheKeyType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ColumnMetadata> getColumns() {
        return m_columns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getValidation() {
        return m_validation;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasColumns() {
        return ! m_columns.isEmpty();
    }

    /**
     * Process the meta-data, configure primary key and idValidation in descriptor.
     */
    public void process(MetadataDescriptor descriptor) {
        descriptor.setHasPrimaryKey();
        
        if (m_validation != null) {
            descriptor.getClassDescriptor().setIdValidation(IdValidation.valueOf(m_validation));
        }
        if (m_cacheKeyType != null) {
            descriptor.getClassDescriptor().setCacheKeyType(CacheKeyType.valueOf(m_cacheKeyType));
        }
        if (hasColumns()) {
            List fields = new ArrayList(m_columns.size());
            for (ColumnMetadata column : m_columns) {
                if (column.getName().equals("")) {  
                    throw ValidationException.optimisticLockingSelectedColumnNamesNotSpecified(descriptor.getJavaClass());
                } else {
                    fields.add(column.getDatabaseField());
                }
            }
            descriptor.getClassDescriptor().setPrimaryKeyFields(fields);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCacheKeyType(String cacheKeyType) {
        m_cacheKeyType = cacheKeyType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumns(List<ColumnMetadata> columns) {
        m_columns = columns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValidation(String validation) {
        m_validation = validation;
    }
}

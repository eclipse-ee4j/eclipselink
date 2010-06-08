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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA relational type colummns into EclipseLink database fields.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public abstract class RelationalColumnMetadata extends MetadataColumn {
    private String m_referencedColumnName;
    
    /**
     * INTERNAL:
     */
    public RelationalColumnMetadata(MetadataAnnotation relationalColumn, MetadataAccessibleObject accessibleObject) {
        super(relationalColumn, accessibleObject);
        
        if (relationalColumn != null) {
            m_referencedColumnName = ((String) relationalColumn.getAttribute("referencedColumnName"));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected RelationalColumnMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof RelationalColumnMetadata) {
            RelationalColumnMetadata relationalColumn = (RelationalColumnMetadata) objectToCompare;
            return valuesMatch(m_referencedColumnName, relationalColumn.getReferencedColumnName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getForeignKeyField() {
        return super.getDatabaseField();
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getPrimaryKeyField() {
        DatabaseField pkField = new DatabaseField();
        
        pkField.setName(m_referencedColumnName == null ? "" : m_referencedColumnName, Helper.getDefaultStartDatabaseDelimiter(), Helper.getDefaultEndDatabaseDelimiter());
        
        return pkField;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getReferencedColumnName() {
        return m_referencedColumnName;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isForeignKeyFieldNotSpecified() {
        return getName() == null || getName().equals("");
    }
    
    /**
     * INTERNAL:
     */
    public boolean isPrimaryKeyFieldNotSpecified() {
        return m_referencedColumnName == null || m_referencedColumnName.equals("");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReferencedColumnName(String referencedColumnName) {
        m_referencedColumnName = referencedColumnName;
    }
}

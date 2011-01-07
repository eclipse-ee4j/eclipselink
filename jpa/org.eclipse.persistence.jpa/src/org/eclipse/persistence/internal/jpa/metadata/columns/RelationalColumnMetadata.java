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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseField;
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
    protected DatabaseField getForeignKeyField() {
        return super.getDatabaseField();
    }
    
    /**
     * INTERNAL:
     * By calling this method we will return the foreign key field with the
     * extra metadata from the primary key field that can not be applied to a 
     * referenced column.
     */
    public DatabaseField getForeignKeyField(DatabaseField primaryKeyField) {
        // Initialize the DatabaseField with values and defaults.
        DatabaseField fkField = getForeignKeyField();
        
        // Primary key field is null in variable one to one case.
        if (primaryKeyField != null) {
            fkField.setLength(primaryKeyField.getLength());
            fkField.setPrecision(primaryKeyField.getPrecision());
            fkField.setScale(primaryKeyField.getScale());
        }
        
        return fkField;
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

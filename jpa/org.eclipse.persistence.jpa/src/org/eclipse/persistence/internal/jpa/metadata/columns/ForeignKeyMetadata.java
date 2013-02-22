/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     12/07/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 *     02/20/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

/**
 * INTERNAL:
 * Object to process JPA foreign key metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5
 */
public class ForeignKeyMetadata extends ORMetadata {
    protected String m_name;
    protected String m_constraintMode;
    protected String m_foreignKeyDefinition;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ForeignKeyMetadata() {
        super("<foreign-key>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading from subclasses of ForeignKeyMetadata.
     */
    public ForeignKeyMetadata(ForeignKeyMetadata foreignKey) {
        super(foreignKey);
        
        m_name = foreignKey.getName();
        m_constraintMode = foreignKey.getConstraintMode();
        m_foreignKeyDefinition = foreignKey.getForeignKeyDefinition();
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ForeignKeyMetadata(MetadataAnnotation foreignKey, MetadataAccessor accessor) {
        super(foreignKey, accessor);
        
        m_name = foreignKey.getAttributeString("name");
        m_constraintMode = foreignKey.getAttributeString("value");
        m_foreignKeyDefinition = foreignKey.getAttributeString("foreignKeyDefinition");
    }
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ForeignKeyMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ForeignKeyMetadata) {
            ForeignKeyMetadata foreignKey = (ForeignKeyMetadata) objectToCompare;
            
            if (! valuesMatch(m_name, foreignKey.getName())) {
                return false;
            }
            
            if (! valuesMatch(m_foreignKeyDefinition, foreignKey.getForeignKeyDefinition())) {
                return false;
            }
            
            return valuesMatch(m_constraintMode, foreignKey.getConstraintMode());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConstraintMode() {
        return m_constraintMode;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getForeignKeyDefinition() {
        return m_foreignKeyDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean isConstraintMode() {
        return m_constraintMode == null || m_constraintMode.equals(MetadataConstants.JPA_CONSTRAINT_MODE_CONSTRAINT);
    }
    
    /**
     * INTERNAL:
     */
    protected boolean isNoConstraintMode() {
        return m_constraintMode != null && m_constraintMode.equals(MetadataConstants.JPA_CONSTRAINT_MODE_NO_CONSTRAINT);
    }
    
    /**
     * INTERNAL:
     */
    protected boolean isProviderDefaultConstraintMode() {
        return m_constraintMode != null && m_constraintMode.equals(MetadataConstants.JPA_CONSTRAINT_MODE_NO_CONSTRAINT);
    }
    
    /**
     * INTERNAL:
     * Process this JPA metadata into an EclipseLink ForeignKeyConstraint.
     */
    public void process(DatabaseTable table) {
        if (! isProviderDefaultConstraintMode()) {
            ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint();
            foreignKeyConstraint.setName(getName());
            foreignKeyConstraint.setForeignKeyDefinition(getForeignKeyDefinition());
            foreignKeyConstraint.setDisableForeignKey(isNoConstraintMode());
            table.addForeignKeyConstraint(foreignKeyConstraint);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConstraintMode(String constraintMode) {
        m_constraintMode = constraintMode;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setForeignKeyDefinition(String foreignKeyDefinition) {
        m_foreignKeyDefinition = foreignKeyDefinition;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }    
}

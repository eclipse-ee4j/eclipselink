/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

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
    protected Boolean m_disableForeignKey;
    
    protected String m_name;
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
        m_foreignKeyDefinition = foreignKey.getForeignKeyDefinition();
        m_disableForeignKey = foreignKey.getDisableForeignKey();
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ForeignKeyMetadata(MetadataAnnotation foreignKey, MetadataAccessor accessor) {
        super(foreignKey, accessor);
        
        if (foreignKey != null) {
            m_name = ((String) foreignKey.getAttribute("name"));
            m_foreignKeyDefinition = ((String) foreignKey.getAttribute("foreignKeyDefinition"));
            m_disableForeignKey = ((Boolean) foreignKey.getAttributeBooleanDefaultFalse("disableForeignKey"));
        }
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
            
            return valuesMatch(m_disableForeignKey, foreignKey.getDisableForeignKey());
        }
        
        return false;
    }
    
    /**
     * INTERNAL
     */
    protected boolean disableForeignKey() {
        return (m_disableForeignKey != null) && m_disableForeignKey.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getDisableForeignKey() {
        return m_disableForeignKey;
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
     * Process this JPA metadata into an EclipseLink ForeignKeyConstraint.
     */
    public ForeignKeyConstraint process() {
        ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint();
        foreignKeyConstraint.setName(getName());
        foreignKeyConstraint.setForeignKeyDefinition(getForeignKeyDefinition());
        foreignKeyConstraint.setDisableForeignKey(disableForeignKey());
        return foreignKeyConstraint;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDisableForeignKey(Boolean disableForeignKey) {
        m_disableForeignKey = disableForeignKey;
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

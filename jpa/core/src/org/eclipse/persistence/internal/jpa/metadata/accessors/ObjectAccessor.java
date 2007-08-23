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

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataJoinColumn;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumn;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.indirection.WeavedObjectBasicIndirectionPolicy;

import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * A single object relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class ObjectAccessor extends RelationshipAccessor {
    protected ObjectAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor)
     * 
     * If a target entity is specified in metadata, it will be set as the 
     * reference class, otherwise we will use the raw class.
     */
    public Class getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetEntity();
        
            if (m_referenceClass == void.class) {
                // Get the reference class from the accessible object and
                // log the defaulting contextual reference class.
                m_referenceClass = super.getReferenceClass();
                m_logger.logConfigMessage(getLoggingContext(), getAnnotatedElement(), m_referenceClass);
            } 
        }
        
        return m_referenceClass;
    }
    
    /**
     * INTERNAL:
     * Initialize a OneToOneMapping.
     */
    protected OneToOneMapping initOneToOneMapping() {
    	OneToOneMapping mapping = new OneToOneMapping();
        mapping.setIsReadOnly(false);
        mapping.setIsPrivateOwned(hasPrivateOwned());
        mapping.setJoinFetch(getMappingJoinFetchType());
        mapping.setIsOptional(isOptional());
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        
        // If the global weave for value holders is true, the use the value
        // from usesIndirection. Otherwise, force it to false.
        boolean usesIndirection = (m_project.enableLazyForOneToOne()) ? usesIndirection() : false;
        if (usesIndirection && m_descriptor.usesPropertyAccess()) {
            mapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(getSetMethodName()));
        } else {
            mapping.setUsesIndirection(usesIndirection);
        }
        
        // Set the getter and setter methods if access is PROPERTY.
        setAccessorMethods(mapping);
        
        // Process the cascade types.
        processCascadeTypes(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        return mapping;
    }
    
    /**
     * INTERNAL:
     * Process the @JoinColumn(s) for the owning side of a one to one mapping.
     * The default pk and pk field names are used only with single primary key 
     * entities. The processor should never get as far as to use them with 
     * entities that have a composite primary key (validation exception will be 
     * thrown).
     */
    protected void processOneToOneForeignKeyRelationship(OneToOneMapping mapping) {         
        // If the pk field (referencedColumnName) is not specified, it 
        // defaults to the primary key of the referenced table.
        String defaultPKFieldName = getReferenceDescriptor().getPrimaryKeyFieldName();
        
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity; "_"; 
        // the name of the referenced primary key column.
        String defaultFKFieldName = getUpperCaseAttributeName() + "_" + defaultPKFieldName;
            
        // Join columns will come from a @JoinColumn(s).
        List<MetadataJoinColumn> joinColumns = processJoinColumns();

        // Add the source foreign key fields to the mapping.
        for (MetadataJoinColumn joinColumn : joinColumns) {
            DatabaseField pkField = joinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, defaultPKFieldName, MetadataLogger.PK_COLUMN));
            pkField.setTable(getReferenceDescriptor().getPrimaryKeyTable());
            
            DatabaseField fkField = joinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, defaultFKFieldName, MetadataLogger.FK_COLUMN));
            // Set the table name if one is not already set.
            if (fkField.getTableName().equals("")) {
                fkField.setTable(m_descriptor.getPrimaryTable());
            }
            
            // Add a source foreign key to the mapping.
            mapping.addForeignKeyField(fkField, pkField);
            
            // If any of the join columns is marked read-only then set the 
            // mapping to be read only.
            if (fkField.isReadOnly()) {
                mapping.setIsReadOnly(true);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the primary key join columns for the owning side of a one to one 
     * mapping. The default pk and pk field names are used only with single 
     * primary key entities. The processor should never get as far as to use 
     * them with entities that have a composite primary key (validation 
     * exception will be thrown).
     */
    protected void processOneToOnePrimaryKeyRelationship(OneToOneMapping mapping) {
        // Join columns will come from a @PrimaryKeyJoinColumn(s).
        MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
        List<MetadataPrimaryKeyJoinColumn> primaryKeyJoinColumns = processPrimaryKeyJoinColumns(getPrimaryKeyJoinColumns(referenceDescriptor.getPrimaryTable(), m_descriptor.getPrimaryTable()));

        // Add the source foreign key fields to the mapping.
        for (MetadataPrimaryKeyJoinColumn primaryKeyJoinColumn : primaryKeyJoinColumns) {
            // The default primary key name is the primary key field name of the
            // referenced entity.
            DatabaseField pkField = primaryKeyJoinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, referenceDescriptor.getPrimaryKeyFieldName(), m_logger.PK_COLUMN));
            
            // The default foreign key name is the primary key of the
            // referencing entity.
            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, m_descriptor.getPrimaryKeyFieldName(), m_logger.FK_COLUMN));
            
            // Add a source foreign key to the mapping.
            mapping.addForeignKeyField(fkField, pkField);
            
            // Mark the mapping read only
            mapping.setIsReadOnly(true);
        }
    }
    
    /**
     * INTERNAL:
     * Process the the correct metadata join column for the owning side of a 
     * one to one mapping.
     */
    protected void processOwningMappingKeys(OneToOneMapping mapping) {
        if (isOneToOnePrimaryKeyRelationship()) {
            processOneToOnePrimaryKeyRelationship(mapping);
        } else {
            processOneToOneForeignKeyRelationship(mapping);
        }
    }
}

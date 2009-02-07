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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;

/**
 * INTERNAL:
 * A OneToMany relationship accessor. A OneToMany annotation currently is not
 * required to be on the accessible object, that is, a 1-M can default.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToManyAccessor extends CollectionAccessor {
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public OneToManyAccessor() {
        super("<one-to-many>");
    }
    
    /**
     * INTERNAL:
     */
    public OneToManyAccessor(Annotation oneToMany, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(oneToMany, accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     * 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return MetadataLogger.ONE_TO_MANY_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isOneToMany() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an OneToMany accessor into an EclipseLink OneToManyMapping. If a 
     * JoinTable is found however, we must create a ManyToManyMapping.
     */
    @Override
    public void process() {
        super.process();
        
        if (getMappedBy() == null || getMappedBy().equals("")) {    
            if (getJoinColumns().isEmpty()) {
                // No join columns and no mapped by value, default to
                // unidirectional 1-M using a M-M mapping and a join table.
                processManyToManyMapping();
            } else {
                // If we find join column(s) then process a uni-directional 1-M.
                processUnidirectionalOneToManyMapping();
            }
        } else {
            // Process a 1-M using the mapped by mapping values.
            processOneToManyMapping();
        }
    }
    
    /**
     * INTERNAL:
     * Process an many to many mapping for this accessor since a join table 
     * was specified.
     */
    protected void processManyToManyMapping() {
        // Create a M-M mapping and process common collection mapping metadata
        // first followed by specific metadata.
        ManyToManyMapping mapping = new ManyToManyMapping();
        process(mapping);
                
        // Process the JoinTable metadata.
        processJoinTable(mapping);
    }
    
    /**
     * INTERNAL:
     * Process an one to many mapping for this accessor.
     */
    protected void processOneToManyMapping() {
        // Create a 1-M mapping and process common collection mapping metadata
        // first followed by specific metadata.
       OneToManyMapping mapping = new OneToManyMapping();
       process(mapping);
            
       // Non-owning side, process the foreign keys from the owner.
       OneToOneMapping ownerMapping = null;
       if (getOwningMapping(getMappedBy()).isOneToOneMapping()){ 
           ownerMapping = (OneToOneMapping) getOwningMapping(getMappedBy());
       } else {
           // If improper mapping encountered, throw an exception.
           throw ValidationException.invalidMapping(getJavaClass(), getReferenceClass()); 
       }
                
       Map<DatabaseField, DatabaseField> keys = ownerMapping.getSourceToTargetKeyFields();
       for (DatabaseField fkField : keys.keySet()) {
           DatabaseField pkField = keys.get(fkField);
                
           // If we are within a table per class strategy we have to update
           // the primary key field to point to our own database table. 
           // The extra table check is if the mapping is actually defined
           // on our java class (meaning we have the right table at this
           // point and can avoid the cloning)
           if (getDescriptor().usesTablePerClassInheritanceStrategy() && ! pkField.getTable().equals(getDescriptor().getPrimaryTable())) {
               // We need to update the pk field to be to our table.
               pkField = (DatabaseField) pkField.clone();
               pkField.setTable(getDescriptor().getPrimaryTable());
           }
            
           mapping.addTargetForeignKeyField(fkField, pkField);
       }   
    }
    
    /**
     * INTERNAL:
     * Process an unidirectional one to many mapping for this accessor since 
     * join columns were specified and no mapped by value. 
     */
    protected void processUnidirectionalOneToManyMapping() {
        // Create a 1-M unidirectional mapping and process common collection 
        // mapping metadata first followed by specific metadata.
        UnidirectionalOneToManyMapping mapping = new UnidirectionalOneToManyMapping();
        process(mapping);
                
        // Process the JoinTable metadata.
        processUnidirectionalOneToManyTargetForeignKeyRelationship(mapping);
    }
    
    /**
     * INTERNAL:
     * Process the @JoinColumn(s) for the owning side of a unidirectional one to many mapping.
     * The default pk used only with single primary key 
     * entities. The processor should never get as far as to use them with 
     * entities that have a composite primary key (validation exception will be 
     * thrown).
     */
    protected void processUnidirectionalOneToManyTargetForeignKeyRelationship(UnidirectionalOneToManyMapping mapping) {         
        // If the pk field (name) is not specified, it 
        // defaults to the primary key of the source table.
        String defaultPKFieldName = getDescriptor().getPrimaryKeyFieldName();
        
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity; "_"; 
        // the name of the referenced primary key column.
        String defaultFKFieldName = getUpperCaseAttributeName() + "_" + defaultPKFieldName;
            
        // Join columns will come from a @JoinColumn(s).
        // Add the source foreign key fields to the mapping.
        for (JoinColumnMetadata joinColumn : processJoinColumns()) {
            DatabaseField pkField = joinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, defaultPKFieldName, MetadataLogger.PK_COLUMN));
            pkField.setTable(getDescriptor().getPrimaryKeyTable());
            
            DatabaseField fkField = joinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, defaultFKFieldName, MetadataLogger.FK_COLUMN));
            // Set the table name if one is not already set.
            if (fkField.getTableName().equals("")) {
                fkField.setTable(getReferenceDescriptor().getPrimaryTable());
            }
            
            // Add target foreign key to the mapping.
            mapping.addTargetForeignKeyField(fkField, pkField);
            
            // If any of the join columns is marked read-only then set the 
            // mapping to be read only.
            if (fkField.isReadOnly()) {
                mapping.setIsReadOnly(true);
            }
        }
    }
}

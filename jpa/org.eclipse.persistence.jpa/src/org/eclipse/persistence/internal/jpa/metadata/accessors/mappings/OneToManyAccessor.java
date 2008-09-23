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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

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
        
        // Should be treated as a uni-directional mapping.
        if (getMappedBy() == null || getMappedBy().equals("")) {
            // If we find a JoinColumn(s) annotations, then throw an exception.
            if (!getJoinColumns().isEmpty()) {
                // Create a 1-M unidirectional mapping and process common collection mapping
                // metadata.
                UnidirectionalOneToManyMapping mapping = new UnidirectionalOneToManyMapping();
                process(mapping);
                
                // Process the JoinTable metadata.
                processUnidirectionalOneToManyTargetForeignKeyRelationship(mapping);
                
                // Process properties
                processProperties(mapping);

                // Add the mapping to the descriptor.
                getDescriptor().addMapping(mapping);
            } else {
            
                // Create a M-M mapping and process common collection mapping
                // metadata.
                ManyToManyMapping mapping = new ManyToManyMapping();
                process(mapping);
                
                // Process the JoinTable metadata.
                processJoinTable(mapping);
                
                // Process properties
                processProperties(mapping);
    
                // Add the mapping to the descriptor.
                getDescriptor().addMapping(mapping);
            }
        } else {
            // Create a 1-M mapping and process common collection mapping
            // metadata.
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
                mapping.addTargetForeignKeyField(fkField, keys.get(fkField));
            }   
            
            // Process properties
            processProperties(mapping);
            
            // Add the mapping to the descriptor.
            getDescriptor().addMapping(mapping);
        }
    }
}

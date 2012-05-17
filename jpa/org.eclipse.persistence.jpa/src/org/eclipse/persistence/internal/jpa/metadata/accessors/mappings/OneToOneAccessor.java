/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     05/17/2012-2.3.3 Arron Ferguson  
 *       - 379829: NPE Thrown with OneToOne Relationship
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;

/**
 * A one to one relationship accessor. A OneToOne annotation currently is not
 * required to be on the accessible object, that is, a 1-1 can default.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToOneAccessor extends ObjectAccessor {
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public OneToOneAccessor() {
        super("<one-to-one>");
    }
    
    /**
     * INTERNAL:
     */
    public OneToOneAccessor(MetadataAnnotation oneToOne, MetadataAnnotatedElement annotatedElement, ClassAccessor classAccessor) {
        super(oneToOne, annotatedElement, classAccessor);
        
        // A one to one mapping can default.
        if (oneToOne != null) {
            setMappedBy((String) oneToOne.getAttribute("mappedBy"));
            setOrphanRemoval((Boolean) oneToOne.getAttribute("orphanRemoval"));
        }
    }
    
    /**
     * INTERNAL:
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return MetadataLogger.ONE_TO_ONE_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isOneToOne() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a one to one setting into an EclipseLink OneToOneMapping.
     */
    @Override
    public void process() {
        super.process();
        
        // Initialize our mapping now with what we found.
        ObjectReferenceMapping mapping = initOneToOneMapping();
        
        if (hasMappedBy()) {
            // Non-owning side, process the foreign keys from the owner.
            DatabaseMapping owningMapping = getOwningMapping();
            if (owningMapping.isOneToOneMapping()){
                OneToOneMapping ownerMapping = (OneToOneMapping) owningMapping;
                
                // If the owner uses a relation table, we need to map the keys
                // as we would for a many-to-many mapping.
                if (ownerMapping.hasRelationTableMechanism()) {
                    // Put a relation table mechanism on our mapping.
                    ((OneToOneMapping)mapping).setRelationTableMechanism(new RelationTableMechanism());
                    processMappedByRelationTable(ownerMapping.getRelationTableMechanism(), ((OneToOneMapping)mapping).getRelationTableMechanism());
                } else {
                    Map<DatabaseField, DatabaseField> targetToSourceKeyFields;
                    Map<DatabaseField, DatabaseField> sourceToTargetKeyFields;
                    
                    // If we are within a table per class strategy we have to update
                    // the primary key field to point to our own database table. 
                    if (getDescriptor().usesTablePerClassInheritanceStrategy()) {
                        targetToSourceKeyFields = new HashMap<DatabaseField, DatabaseField>();
                        sourceToTargetKeyFields = new HashMap<DatabaseField, DatabaseField>();
                        
                        for (DatabaseField fkField : ownerMapping.getSourceToTargetKeyFields().keySet()) {
                            // We need to update the pk field to be to our table.
                            DatabaseField pkField = ownerMapping.getSourceToTargetKeyFields().get(fkField).clone();
                            pkField.setTable(getDescriptor().getPrimaryTable());
                            sourceToTargetKeyFields.put(fkField, pkField);
                            targetToSourceKeyFields.put(pkField, fkField);
                        }
                    } else {
                        targetToSourceKeyFields = ownerMapping.getTargetToSourceKeyFields();
                        sourceToTargetKeyFields = ownerMapping.getSourceToTargetKeyFields();
                    }
                    
                    ((OneToOneMapping)mapping).setSourceToTargetKeyFields(targetToSourceKeyFields);
                    ((OneToOneMapping)mapping).setTargetToSourceKeyFields(sourceToTargetKeyFields);
                }
            } else {
                // If improper mapping encountered, throw an exception.
                throw ValidationException.invalidMapping(getJavaClass(), getReferenceClass());
            }
        } else if (mapping instanceof OneToOneMapping) {
            // Owning side, look for JoinColumns or PrimaryKeyJoinColumns.
            processOwningMappingKeys(((OneToOneMapping)mapping));
        } else {
            processForeignKeyRelationship(mapping);
        }
    }   
    
}

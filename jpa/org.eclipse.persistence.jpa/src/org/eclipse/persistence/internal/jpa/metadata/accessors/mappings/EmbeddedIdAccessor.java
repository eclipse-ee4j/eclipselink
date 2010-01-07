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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.HashMap;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.mappings.EmbeddableMapping;

/**
 * An embedded id relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class EmbeddedIdAccessor extends EmbeddedAccessor {
    // We store map of fields that are the primary key and add them only at the
    // end of processing since they may change when processing attribute 
    // overrides. They are mapped by attribute name.
    protected HashMap<String, DatabaseField> m_idFields = new HashMap<String, DatabaseField>();
    protected HashMap<DatabaseField, MappingAccessor> m_idAccessors = new HashMap<DatabaseField, MappingAccessor>();
    
    /**
     * INTERNAL:
     * Default constructor.
     */
    public EmbeddedIdAccessor() {
        super("<embedded-id>");
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddedIdAccessor(MetadataAnnotation embeddedId, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(embeddedId, accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     * Process an attribute override for an embedded object, that is, an 
     * aggregate object mapping in EclipseLink.
     */
    @Override
    protected void addFieldNameTranslation(EmbeddableMapping embeddableMapping, String overrideName, DatabaseField overrideField, MappingAccessor mappingAccessor) {
       super.addFieldNameTranslation(embeddableMapping, overrideName, overrideField, mappingAccessor);
       
       // Update our primary key field with the attribute override field.
       // The super class will ensure the correct field is on the metadata
       // column.
       m_idFields.put(mappingAccessor.getAttributeName(), overrideField);
    }
    
    /**
     * INTERNAL:
     */
    protected void addIdFieldFromAccessor(MappingAccessor accessor) {
        String attributeName = accessor.getAttributeName();
        
        if (m_idFields.containsKey(attributeName)) {
            // It may be in our id fields map already if an attribute override 
            // was specified on the embedded mapping. Make sure the existing id 
            // field has its mapping accessor associated with it.
            m_idAccessors.put(m_idFields.get(attributeName), accessor);
        } else {
            DatabaseField field = accessor.getMapping().getField();
            m_idFields.put(attributeName, field);
            m_idAccessors.put(field, accessor);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isEmbeddedId() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an EmbeddedId metadata.
     */    
    @Override
    public void process() {
        // Process the embeddable and our embedded metadata. This must be
        // done now and before the calls below.
        super.process();
        
        // After processing the embeddable class, we need to gather our 
        // primary keys fields that we will eventually set on the owning 
        // descriptor metadata.
        if (getReferenceDescriptor().getAccessors().isEmpty()) {
            throw ValidationException.embeddedIdHasNoAttributes(getDescriptor().getJavaClass(), getReferenceDescriptor().getJavaClass(), getReferenceDescriptor().getClassAccessor().getAccessType());
        } else {
            // Go through all our mappings, the fields from those mappings will
            // make up the composite primary key.
            for (MappingAccessor accessor : getReferenceAccessors()) {
                if (accessor.isBasic()) {
                    addIdFieldFromAccessor(accessor);
                } else if (accessor.isDerivedIdClass()) {
                    for (MappingAccessor embeddedAccessor : accessor.getReferenceAccessors()) {
                        addIdFieldFromAccessor(embeddedAccessor);
                    }
                } else {
                    // EmbeddedId is solely a JPA feature, so we will not allow 
                    // the expansion of attributes for those types of Embeddable 
                    // classes beyond basics or derived ids as defined in the spec.
                    throw ValidationException.invalidMappingForEmbeddedId(getAttributeName(), getJavaClass(), accessor.getAttributeName(), getReferenceDescriptor().getJavaClass());
                }
            }
        
            // Set the embedded id metadata on all owning descriptors.
            for (MetadataDescriptor owningDescriptor : this.getOwningDescriptors()) {
                // Check if we already processed an EmbeddedId for this entity.
                if (owningDescriptor.hasEmbeddedId()) {
                    throw ValidationException.multipleEmbeddedIdAnnotationsFound(getJavaClass(), getAttributeName(), owningDescriptor.getEmbeddedIdAttributeName());
                } 
            
                // Check if we already processed an Id or IdClass.
                if (owningDescriptor.hasPrimaryKeyFields()) {
                    throw ValidationException.embeddedIdAndIdAnnotationFound(getJavaClass(), getAttributeName(), owningDescriptor.getIdAttributeName());
                }
                
                // Set the PK class.
                owningDescriptor.setPKClass(getReferenceClass());
                    
                // Store the embeddedId attribute name.
                owningDescriptor.setEmbeddedIdAccessor(this);
                
                // Add all the fields from the embeddable as primary keys on the 
                // owning metadata descriptor.
                for (DatabaseField field : m_idFields.values()) {
                    if (! owningDescriptor.getPrimaryKeyFieldNames().contains(field.getName())) {
                        // Set a table if one is not specified. Because embeddables 
                        // can be re-used we must deal with clones and not change 
                        // the original fields.
                        DatabaseField clone = (DatabaseField) field.clone();
                        if (clone.getTableName().equals("")) {
                            clone.setTable(owningDescriptor.getPrimaryTable());
                        }
                    
                        owningDescriptor.addPrimaryKeyField(clone, m_idAccessors.get(clone));
                    }
                }
            }
        }
    }
}

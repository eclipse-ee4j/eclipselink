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

import java.util.HashMap;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

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

    /**
     * INTERNAL:
     */
    public EmbeddedIdAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL: (Override from MetadataAccesor)
     */
	public boolean isEmbeddedId() {
        return true;
    }
    
    /**
     * INTERNAL: (Override from EmbeddedAccessor)
     * 
     * Process an @EmbeddedId or embedded-id element.
     */    
    public void process() {
        if (m_descriptor.ignoreIDs()) {
            // XML/Annotation merging. XML wins, ignore annotations.
            m_logger.logWarningMessage(m_logger.IGNORE_EMBEDDED_ID, this);
        } else {
            // Check if we already processed an EmbeddedId for this entity.
            if (m_descriptor.hasEmbeddedIdAttribute()) {
                m_validator.throwMultipleEmbeddedIdsFound(getJavaClass(), getAttributeName(), m_descriptor.getEmbeddedIdAttributeName());
            } 
            
            // Check if we already processed an Id or IdClass.
            if (m_descriptor.hasPrimaryKeyFields()) {
                m_validator.throwEmbeddedIdAndIdFound(getJavaClass(), getAttributeName(), m_descriptor.getIdAttributeName());
            }
            
            // Set the PK class.
            m_descriptor.setPKClass(getReferenceClass());
            
            // Store the embeddedId attribute name.
            m_descriptor.setEmbeddedIdAttributeName(getAttributeName());
        }
        
        // Process the embeddable mapping specifics.
        super.process();
            
        // Add the fields from the embeddable as primary keys on the owning
        // metadata descriptor.
        for (DatabaseField field : m_idFields.values()) {
            m_descriptor.addPrimaryKeyField(field);
        }
    }
    
    /**
     * INTERNAL: (Override from EmbeddedAccesor)
     * 
     * Process an @AttributeOverride or attribute-override element for an 
     * embedded object, that is, an aggregate object mapping in TopLink.
	 */
	protected void processAttributeOverride(AggregateObjectMapping mapping, MetadataColumn column) {
        super.processAttributeOverride(mapping, column);
        
        // Update our primary key field with the attribute override field.
        // The super class with ensure the correct field is on the metadata
        // column.
        DatabaseField field = column.getDatabaseField();
        field.setTable(m_descriptor.getPrimaryTable());
        m_idFields.put(column.getAttributeName(), field);
	}
    
    /**
     * INTERNAL: (Override from EmbeddedAccesor)
     *
     * Process the embeddable class and gather up our 'original' collection of
     * primary key fields. They are original because they may change with the
     * specification of an attribute override.
     */
    protected MetadataDescriptor processEmbeddableClass() {
        MetadataDescriptor embeddableDescriptor = super.processEmbeddableClass();
        
        // After processing the embeddable class, we need to gather our 
        // primary keys fields that we will eventually set on the owning 
        // descriptor metadata.
        if (isEmbeddedId() && ! m_descriptor.ignoreIDs()) {
            if (embeddableDescriptor.getMappings().isEmpty()) {
                String accessType = embeddableDescriptor.usesPropertyAccess() ? AccessType.PROPERTY.name() : AccessType.FIELD.name();
                m_validator.throwEmbeddedIdHasNoAttributes(m_descriptor.getJavaClass(), embeddableDescriptor.getJavaClass(), accessType);
            }

            for (DatabaseMapping mapping : embeddableDescriptor.getMappings()) {
                DatabaseField field = (DatabaseField) mapping.getField().clone();
                field.setTable(m_descriptor.getPrimaryTable());
                m_idFields.put(mapping.getAttributeName(), field);
            }
        }
        
        return embeddableDescriptor;
    }
}

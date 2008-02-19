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

import javax.persistence.ManyToOne;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * A many to one relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ManyToOneAccessor extends ObjectAccessor {
	/**
     * INTERNAL:
     */
    public ManyToOneAccessor() {}
    
    /**
     * INTERNAL:
     */
    public ManyToOneAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        ManyToOne manyToOne = getAnnotation(ManyToOne.class);
        
        setTargetEntity(manyToOne.targetEntity());
        setCascadeTypes(manyToOne.cascade());
        setFetch(manyToOne.fetch());
        setOptional(manyToOne.optional());
    }

    /**
     * INTERNAL:
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return MetadataLogger.MANY_TO_ONE_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL: 
     * Mapped by is not supported on this accessor, this just ensures no 
     * metadata processing code tries to call it on this accessor.
     */
    public String getMappedBy() {
        throw new RuntimeException("Development exception. A mapped by value is not supported on a many to one.");
    }
    
    /**
     * INTERNAL: (Override from RelationshipAccessor)
     * A PrivateOwned annotation used with a ManyToOne annotation is ignored. 
     * A log warning is issued.
     */
	protected boolean hasPrivateOwned() {
        if (super.hasPrivateOwned()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_PRIVATE_OWNED_ANNOTATION, this);
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
	public boolean isManyToOne() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a many to one setting into an EclipseLink OneToOneMapping.
     */
    public void process() {
        // Initialize our mapping now with what we found.
        OneToOneMapping mapping = initOneToOneMapping();

        // Now process the JoinColumns (if there are any) for this mapping.
        processOwningMappingKeys(mapping);
        
        // Add the mapping to the descriptor.
        getDescriptor().addMapping(mapping);
    }
}

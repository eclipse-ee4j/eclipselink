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
    private ManyToOne m_manyToOne;
    
    /**
     * INTERNAL:
     */
    public ManyToOneAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_manyToOne = getAnnotation(ManyToOne.class);
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToOneAccessor)
     */
    public List<String> getCascadeTypes() {
        return getCascadeTypes(m_manyToOne.cascade());
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToOneAccessor)
     */
    public String getFetchType() {
        return m_manyToOne.fetch().name();
    }

    /**
     * INTERNAL:
     * 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return m_logger.MANY_TO_ONE_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToOneAccessor)
     */
    public Class getTargetEntity() {
        return m_manyToOne.targetEntity();
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     * 
     * A @PrivateOwned used with a @ManyToOne is ignored. A log warning is
     * issued.
     */
	protected boolean hasPrivateOwned() {
        if (super.hasPrivateOwned()) {
            // Annotation specific message no private owned in XML yet. Will
            // have to change when introduced in XML.
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
     * INTERNAL: (Overridden in XMLManyToOneAccessor)
     */
    public boolean isOptional() {
        return m_manyToOne.optional();
    }
    
    /**
     * INTERNAL:
     * Process a @ManyToOne or many-to-one element into a TopLink OneToOne 
     * mapping.
     */
    public void process() {
        // Initialize our mapping now with what we found.
        OneToOneMapping mapping = initOneToOneMapping();

        // Now process the JoinColumns (if there are any) for this mapping.
        processOwningMappingKeys(mapping);
        
        // Add the mapping to the descriptor.
        m_descriptor.addMapping(mapping);
    }
}

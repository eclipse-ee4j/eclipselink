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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToOne;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * A one to one relationship accessor. A @OneToOne annotation currently is not
 * required to be on the accessible object, that is, a 1-1 can default.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToOneAccessor extends ObjectAccessor {
    private OneToOne m_oneToOne;
    
    /**
     * INTERNAL:
     */
    public OneToOneAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_oneToOne = getAnnotation(OneToOne.class);
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToOneAccessor)
     */
    public List<String> getCascadeTypes() {
        if (m_oneToOne == null) {
            return new ArrayList<String>();
        } else {
            return getCascadeTypes(m_oneToOne.cascade());
        } 
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToOneAccessor)
     */
    public String getFetchType() {
        return (m_oneToOne == null) ? MetadataConstants.EAGER : m_oneToOne.fetch().name();
    }
    
    /**
     * INTERNAL:
     * 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return m_logger.ONE_TO_ONE_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToOneAccessor)
     */
    public String getMappedBy() {
        return (m_oneToOne == null) ? "" : m_oneToOne.mappedBy();
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToOneAccessor)
     */
    public Class getTargetEntity() {
        return (m_oneToOne == null) ? void.class : m_oneToOne.targetEntity();
    }
    
    /**
     * INTERNAL:
     */
	public boolean isOneToOne() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isOptional() {
        return (m_oneToOne == null) ? true : m_oneToOne.optional();
    }
    
    /**
     * INTERNAL:
     * Process a @OneToOne or one-to-one element into a TopLink OneToOne 
     * mapping.
     */
    public void process() {
        // Initialize our mapping now with what we found.
        OneToOneMapping mapping = initOneToOneMapping();
        
        if (getMappedBy().equals("")) {
            // Owning side, look for JoinColumns or PrimaryKeyJoinColumns.
            processOwningMappingKeys(mapping);
        } else {	
            // Non-owning side, process the foreign keys from the owner.
            OneToOneMapping ownerMapping = null;
            if (getOwningMapping().isOneToOneMapping()){
            	ownerMapping = (OneToOneMapping)getOwningMapping();
            } else {
            	// If improper mapping encountered, throw an exception.
            	getValidator().throwInvalidMappingEncountered(getJavaClass(), getReferenceClass());
            }

            mapping.setSourceToTargetKeyFields(ownerMapping.getTargetToSourceKeyFields());
            mapping.setTargetToSourceKeyFields(ownerMapping.getSourceToTargetKeyFields());
        }
        
        // Add the mapping to the descriptor.
        m_descriptor.addMapping(mapping);
    }
}

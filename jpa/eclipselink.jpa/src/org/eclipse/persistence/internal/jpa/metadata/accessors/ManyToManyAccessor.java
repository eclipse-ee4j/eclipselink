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

import javax.persistence.ManyToMany;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.mappings.ManyToManyMapping;

/**
 * A many to many relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ManyToManyAccessor extends CollectionAccessor {    
    private ManyToMany m_manyToMany;
    
    /**
     * INTERNAL:
     */
    public ManyToManyAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_manyToMany = getAnnotation(ManyToMany.class);
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToManyAccessor)
     */
    public List<String> getCascadeTypes() {
        return getCascadeTypes(m_manyToMany.cascade());
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToManyAccessor)
     */
    public String getFetchType() {
        return m_manyToMany.fetch().name();
    }
    
    /**
     * INTERNAL:
     * 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return m_logger.MANY_TO_MANY_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToManyAccessor)
     */
    public String getMappedBy() {
        return m_manyToMany.mappedBy();
    }
    
    /**
     * INTERNAL: (Overridden in XMLManyToManyAccessor)
     */
    public Class getTargetEntity() {
        return m_manyToMany.targetEntity();
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     * 
	 * A @PrivateOwned used with a @ManyToMany is ignored. A log warning is
     * issued.
     */
	protected boolean hasPrivateOwned() {
        if (super.hasPrivateOwned()) {
            // Annotation specific message since no private owned in XML yet. 
            // Will have to change when introduced in XML.
            getLogger().logWarningMessage(MetadataLogger.IGNORE_PRIVATE_OWNED_ANNOTATION, this);
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
	public boolean isManyToMany() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a @ManyToMany or many-to-many element into a TopLink MnayToMany 
     * mapping.
     */
    public void process() {
        // Create a M-M mapping and process common collection mapping metadata.
        ManyToManyMapping mapping = new ManyToManyMapping();
        process(mapping);

        if (getMappedBy().equals("")) { 
            // Processing the owning side of a M-M that is process a join table.
            processJoinTable(getJoinTable(), mapping);
        } else {
            // We are processing the a non-owning side of a M-M. Must set the
            // mapping read-only.
            mapping.setIsReadOnly(true);
            
            // Get the owning mapping from the reference descriptor metadata.
            ManyToManyMapping ownerMapping = null;
            if (getOwningMapping().isManyToManyMapping()){
            	ownerMapping = (ManyToManyMapping)getOwningMapping();
            } else {
            	// If improper mapping encountered, throw an exception.
            	getValidator().throwInvalidMappingEncountered(getJavaClass(), getReferenceClass());
            }

            // Set the relation table name from the owner.
	        mapping.setRelationTable(ownerMapping.getRelationTable());
	             
	        // Add all the source foreign keys we found on the owner.
	        mapping.setSourceKeyFields(ownerMapping.getTargetKeyFields());
	        mapping.setSourceRelationKeyFields(ownerMapping.getTargetRelationKeyFields());
	            
	        // Add all the target foreign keys we found on the owner.
	        mapping.setTargetKeyFields(ownerMapping.getSourceKeyFields());
	        mapping.setTargetRelationKeyFields(ownerMapping.getSourceRelationKeyFields());
        }

        // Add the mapping to the descriptor.
        m_descriptor.addMapping(mapping);
    }
}

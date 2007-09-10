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

import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * A OneToMany relationship accessor. A @OneToMany annotation currently is not
 * required to be on the accessible object, that is, a 1-M can default.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToManyAccessor extends CollectionAccessor {
    private OneToMany m_oneToMany;
    
    /**
     * INTERNAL:
     */
    public OneToManyAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_oneToMany = getAnnotation(OneToMany.class);
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToManyAccessor)
     */
    public List<String> getCascadeTypes() {
        if (m_oneToMany == null) {
            return new ArrayList<String>();
        } else {
            return getCascadeTypes(m_oneToMany.cascade());
        } 
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToManyAccessor)
     */
    public String getFetchType() {
        return (m_oneToMany == null) ? MetadataConstants.LAZY : m_oneToMany.fetch().name();
    }
    
    /**
     * INTERNAL:
     * 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return m_logger.ONE_TO_MANY_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToManyAccessor)
     */
    public String getMappedBy() {
        return (m_oneToMany == null) ? "" : m_oneToMany.mappedBy();
    }
    
    /**
     * INTERNAL: (Overridden in XMLOneToManyAccessor)
     */
    public Class getTargetEntity() {
        return (m_oneToMany == null) ? void.class : m_oneToMany.targetEntity();
    }
    
    /**
     * INTERNAL:
     */
	public boolean isOneToMany() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an @OneToMany or one-to-many element into a TopLink OneToMany 
     * mapping. If a JoinTable is found however, we must create a ManyToMany 
     * mapping.
     */
    public void process() {
        String mappedBy = getMappedBy();
        
        // Should be treated as a uni-directional mapping using a join table.
        if (mappedBy.equals("")) {
            // If we find a JoinColumn(s), then throw an exception.
            if (hasJoinColumn() || hasJoinColumns()) {
                getValidator().throwUniDirectionalOneToManyHasJoinColumnSpecified(getAttributeName(), getJavaClass());
            }
            
            // Create a M-M mapping and process common collection mapping
            // metadata.
            ManyToManyMapping mapping = new ManyToManyMapping();
            process(mapping);
            
            // If we found a @PrivateOwned for this mapping, turn it off and
            // log a warning to the user that we are ignoring it.
            if (mapping.isPrivateOwned()) {
                // Annotation specific message since no private owned in XML yet. 
                // Will have to change when introduced in XML.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_PRIVATE_OWNED_ANNOTATION, this);
                mapping.setIsPrivateOwned(false);
            }
            
            // Process the @JoinTable.
            processJoinTable(getJoinTable(), mapping);
            
            // Add the mapping to the descriptor.
            m_descriptor.addMapping(mapping);
        } else {
            // Create a 1-M mapping and process common collection mapping
            // metadata.
            OneToManyMapping mapping = new OneToManyMapping();
            process(mapping);
            
            // Non-owning side, process the foreign keys from the owner.
			OneToOneMapping ownerMapping = null;
            if (getOwningMapping().isOneToOneMapping()){ 
            	ownerMapping = (OneToOneMapping) getOwningMapping();
            } else {
				// If improper mapping encountered, throw an exception.
            	getValidator().throwInvalidMappingEncountered(getJavaClass(), getReferenceClass()); 
            }
                
            Map<DatabaseField, DatabaseField> keys = ownerMapping.getSourceToTargetKeyFields();
            for (DatabaseField fkField : keys.keySet()) {
                mapping.addTargetForeignKeyField(fkField, keys.get(fkField));
            }   
            
            // Add the mapping to the descriptor.
            m_descriptor.addMapping(mapping);
        }
    }
}

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

import java.util.Map;
import javax.persistence.OneToMany;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * A OneToMany relationship accessor. A OneToMany annotation currently is not
 * required to be on the accessible object, that is, a 1-M can default.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToManyAccessor extends CollectionAccessor {
	/**
     * INTERNAL:
     */
    public OneToManyAccessor() {}
    
    /**
     * INTERNAL:
     */
    public OneToManyAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        OneToMany oneToMany = getAnnotation(OneToMany.class);
        
        // We must check because OneToMany's can default.
        if (oneToMany != null) {
        	setTargetEntity(oneToMany.targetEntity());
        	setCascadeTypes(oneToMany.cascade());
        	setFetch(oneToMany.fetch());
        	setMappedBy(oneToMany.mappedBy());
        }
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
	public boolean isOneToMany() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an OneToMany accessor into an EclipseLink OneToManyMapping. If a 
     * JoinTable is found however, we must create a ManyToManyMapping.
     */
    public void process() {
    	super.process();
        String mappedBy = getMappedBy();
        
        // Should be treated as a uni-directional mapping using a join table.
        if (mappedBy.equals("")) {
            // If we find a JoinColumn(s) annotations, then throw an exception.
            if (hasJoinColumn() || hasJoinColumns()) {
            	throw ValidationException.uniDirectionalOneToManyHasJoinColumnAnnotations(getAttributeName(), getJavaClass());
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
            
            // Process the JoinTable metadata.
            processJoinTable(mapping);
            
            // Add the mapping to the descriptor.
            getDescriptor().addMapping(mapping);
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
            	throw ValidationException.invalidMapping(getJavaClass(), getReferenceClass()); 
            }
                
            Map<DatabaseField, DatabaseField> keys = ownerMapping.getSourceToTargetKeyFields();
            for (DatabaseField fkField : keys.keySet()) {
                mapping.addTargetForeignKeyField(fkField, keys.get(fkField));
            }   
            
            // Add the mapping to the descriptor.
            getDescriptor().addMapping(mapping);
        }
    }
}

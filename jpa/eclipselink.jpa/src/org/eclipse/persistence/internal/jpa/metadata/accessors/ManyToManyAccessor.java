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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import javax.persistence.ManyToMany;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

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
	/**
     * INTERNAL:
     */
    public ManyToManyAccessor() {}
    
    /**
     * INTERNAL:
     */
    public ManyToManyAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        Object manyToMany = getAnnotation(ManyToMany.class);
        
        setTargetEntity((Class)invokeMethod("targetEntity", manyToMany));
        setCascadeTypes((Enum[])invokeMethod("cascade", manyToMany));
        setFetch((Enum)invokeMethod("fetch", manyToMany));
        setMappedBy((String)invokeMethod("mappedBy", manyToMany));
    }
    
    /**
     * INTERNAL: 
     * Return the logging context for this accessor.
     */
    protected String getLoggingContext() {
        return MetadataLogger.MANY_TO_MANY_MAPPING_REFERENCE_CLASS;
    }
    
    /**
     * INTERNAL: (Override from RelationshipAccessor) 
	 * A PrivateOwned setting on a ManyToMany is ignored. A log warning is
     * issued.
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
	public boolean isManyToMany() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a many to many metadata accessor into a EclipseLink 
     * ManyToManyMapping.
     */
    public void process() {
    	super.process();
    	
        // Create a M-M mapping and process common collection mapping metadata.
        ManyToManyMapping mapping = new ManyToManyMapping();
        process(mapping);

        if (getMappedBy().equals("")) { 
            // Processing the owning side of a M-M that is process a join table.
            processJoinTable(mapping);
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
            	throw ValidationException.invalidMapping(getJavaClass(), getReferenceClass());
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
        getDescriptor().addMapping(mapping);
    }
}

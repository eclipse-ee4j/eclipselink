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

import javax.persistence.OneToOne;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * A one to one relationship accessor. A OneToOne annotation currently is not
 * required to be on the accessible object, that is, a 1-1 can default.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class OneToOneAccessor extends ObjectAccessor {
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
    public OneToOneAccessor() {}
    
    /**
     * INTERNAL:
     */
    public OneToOneAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        Object oneToOne = getAnnotation(OneToOne.class);
        
        // We must check because OneToMany's can default.
        if (oneToOne != null) {
        	setTargetEntity((Class)invokeMethod("targetEntity", oneToOne, (Object[])null));
        	setCascadeTypes((Enum[])invokeMethod("cascade", oneToOne, (Object[])null));
        	setFetch((Enum)invokeMethod("fetch", oneToOne, (Object[])null));
        	setOptional((Boolean)invokeMethod("optional", oneToOne, (Object[])null));
        	setMappedBy((String)invokeMethod("mappedBy", oneToOne, (Object[])null));
        } else {
        	// Set the annotation defaults.
        	setTargetEntity(void.class);
        	setCascadeTypes(new Enum[]{});
        	setFetch(getDefaultFetchType());
        	setOptional(true);
        	setMappedBy("");
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
	public boolean isOneToOne() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a one to one setting into an EclipseLink OneToOneMapping.
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
            	throw ValidationException.invalidMapping(getJavaClass(), getReferenceClass());
            }

            mapping.setSourceToTargetKeyFields(ownerMapping.getTargetToSourceKeyFields());
            mapping.setTargetToSourceKeyFields(ownerMapping.getSourceToTargetKeyFields());
        }
        
        // Add the mapping to the descriptor.
        getDescriptor().addMapping(mapping);
    }
}

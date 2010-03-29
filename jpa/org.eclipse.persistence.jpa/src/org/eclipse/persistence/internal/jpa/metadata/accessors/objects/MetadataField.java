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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * INTERNAL:
 * An object to hold onto a valid JPA decorated field.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class MetadataField extends MetadataAnnotatedElement {
    protected MetadataClass declaringClass;
    
    /**
     * INTERNAL:
     */
    public MetadataField(MetadataClass metadataClass) {
        super(metadataClass.getMetadataFactory());
        this.declaringClass = metadataClass;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getDeclaringClass() {
        return declaringClass;
    }
    
    /**
     * INTERNAL:
     * Return true is this field is a valid persistence field. This method
     * will validate against any declared annotations on the field. If the 
     * mustBeExplicit flag is true, then we are processing the inverse of an 
     * explicit access setting and the field must have an Access(FIELD) 
     * setting to be processed. Otherwise, it is ignored.
     */
    public boolean isValidPersistenceField(boolean mustBeExplicit, MetadataDescriptor descriptor) {
        if (isValidPersistenceElement(mustBeExplicit, MetadataConstants.FIELD, descriptor)) {
            return isValidPersistenceField(descriptor, hasDeclaredAnnotations(descriptor)); 
        }

        return false;
    }
    
    /**
     * INTERNAL:
     * Return true is this field is a valid persistence field. User decorated
     * is used to indicate that the field either had persistence annotations
     * defined on it or that it was specified in XML.
     */
    public boolean isValidPersistenceField(MetadataDescriptor descriptor, boolean userDecorated) {
        if (! isValidPersistenceElement(getModifiers())) {
            if (userDecorated) {
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPING_METADATA, this, descriptor.getJavaClass());
            }
            
            return false;
        }
        
        return true;
    }

    /**
     * INTERNAL:
     */
    public void setDeclaringClass(MetadataClass declaringClass) {
        this.declaringClass = declaringClass;
    }
}

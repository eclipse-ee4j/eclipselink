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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     08/26/2008-1.0.1 Guy Pelletier 
 *       - 229821: Sequencing does not work with EmbeddableID
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * A relational accessor. A Basic annotation may or may not be present on the
 * accessible object.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class IdAccessor extends BasicAccessor {
    /**
     * INTERNAL:
     */
    public IdAccessor() {
        super("<id>");
    }
    
    /**
     * INTERNAL:
     */
    public IdAccessor(Annotation id, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(id, accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     * Process an id accessor.
     */
    @Override
    public void process() {
        // This will initialize the m_field variable. Accessible through getField().
        super.process();
        
        String attributeName = getAttributeName();

        if (getOwningDescriptor().hasEmbeddedIdAttribute()) {
            // We found both an Id and an EmbeddedId, throw an exception.
            throw ValidationException.embeddedIdAndIdAnnotationFound(getJavaClass(), getOwningDescriptor().getEmbeddedIdAttributeName(), attributeName);
        }

        // If this entity has a pk class, we need to validate our ids. 
        getOwningDescriptor().validatePKClassId(attributeName, getReferenceClass());

        // Store the Id attribute name. Used with validation and OrderBy.
        getOwningDescriptor().addIdAttributeName(attributeName);

        // Add the primary key field to the descriptor.            
        getOwningDescriptor().addPrimaryKeyField(getField());
    }
}

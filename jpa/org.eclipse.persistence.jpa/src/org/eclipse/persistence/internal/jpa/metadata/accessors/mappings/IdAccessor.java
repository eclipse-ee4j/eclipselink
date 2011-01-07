/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     08/04/2010-2.1.1 Guy Pelletier
 *       - 315782: JPA2 derived identity metadata processing validation doesn't account for autoboxing
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * A relational accessor. A Basic annotation may or may not be present on the
 * accessible object.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class IdAccessor extends BasicAccessor {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.
    
    /**
     * INTERNAL:
     */
    public IdAccessor() {
        super("<id>");
    }
    
    /**
     * INTERNAL:
     */
    public IdAccessor(MetadataAnnotation id, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(id, accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof IdAccessor;
    }
    
    /**
     * INTERNAL:
     * Marks this accessor as part of the Id
     */
    public boolean isId(){
        return true;
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

        for (MetadataDescriptor owningDescriptor : getOwningDescriptors()) {
            if (owningDescriptor.hasEmbeddedId()) {
                // We found both an Id and an EmbeddedId, throw an exception.
                throw ValidationException.embeddedIdAndIdAnnotationFound(getJavaClass(), owningDescriptor.getEmbeddedIdAttributeName(), attributeName);
            }

            // If this entity has a pk class, we need to validate our ids. 
            owningDescriptor.validatePKClassId(attributeName, getBoxedType(getReferenceClassName()));

            // Store the Id attribute name. Used with validation and OrderBy.
            owningDescriptor.addIdAttributeName(attributeName);

            // Add the primary key field to the descriptor.            
            owningDescriptor.addPrimaryKeyField(getField(), this);
        }
        
        // Flag this id accessor as a JPA id mapping.
        getMapping().setIsJPAId();
    }
}

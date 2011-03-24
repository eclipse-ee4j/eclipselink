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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;

/**
 * A derived id class accessor is found within an entity's embedded id class
 * and is a reference back to a parents id class.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class msst be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class DerivedIdClassAccessor extends EmbeddedAccessor {
    /**
     * INTERNAL:
     * Constructor called from an Embedabble IdClass.
     */
    public DerivedIdClassAccessor(MetadataAnnotatedElement accessibleObject, ClassAccessor classAccessor) {
        super(null, accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof DerivedIdClassAccessor;
    }

    /**
     * INTERNAL:
     * Return true if this accessor is a derived id class accessor. That is,
     * an EmbeddedId that has a portion of its accessors mapped through an
     * owning entities ManyToOne relationship using a MapsId.
     */
    @Override
    public boolean isDerivedIdClass(){
        return true;
    }
}

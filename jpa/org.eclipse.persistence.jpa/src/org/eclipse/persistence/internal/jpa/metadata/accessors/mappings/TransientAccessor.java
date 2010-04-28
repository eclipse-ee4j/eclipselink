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
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;

/**
 * INTERNAL:
 * An transient accessor ... which does nothing ... just a clever way to
 * make sure we don't process the accessible object for annotations.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class TransientAccessor extends MappingAccessor {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    /**
     * INTERNAL:
     */
    public TransientAccessor() {
        super("<transient>");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) && objectToCompare instanceof TransientAccessor;
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a transient mapping.
     */
    public boolean isTransient() {
        return true;
    }
    
    /**
     * INTERNAL:
     */
    public void process() {
        // Does nothing ...
    }
}

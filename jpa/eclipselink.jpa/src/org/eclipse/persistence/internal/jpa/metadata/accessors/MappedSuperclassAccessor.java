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

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

/**
 * A mapped superclass accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MappedSuperclassAccessor extends ClassAccessor {
	/**
     * INTERNAL:
     */
    public MappedSuperclassAccessor() {}
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor(Class cls, MetadataDescriptor descriptor, MetadataProject project) {
    	super(cls, descriptor, project);
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on a mapped superclass.
     */
    public void process() {
        // Process the common class level attributes that an entity or
        // mapped superclass may define.
        processClassMetadata();
            
        // Process the accessors from the mapped superclass.
        processAccessors();
    }
}

/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.sessions;

import java.io.Serializable;
import java.util.List;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;

public abstract class CoreProject <
   DESCRIPTOR extends CoreDescriptor
   > implements Serializable {

	public abstract void addDescriptor(DESCRIPTOR descriptor);

    /**
     * PUBLIC:
     * Return the descriptor specified for the class.
     */
    public abstract DESCRIPTOR getDescriptor(Class theClass);
    
    /**
     * INTERNAL:
     * Return the descriptors in the order added.
     * Used to maintain consistent order in XML.
     */
    public abstract List<DESCRIPTOR> getOrderedDescriptors();
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this project to actual class-based settings.
     * This will also reset any class references to the version of the class from the class loader.
     */
    public abstract void convertClassNamesToClasses(ClassLoader classLoader);
}

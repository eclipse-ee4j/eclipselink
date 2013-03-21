/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.queries;

import java.util.*;

import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * PERF: Avoids reflection usage for IndirectList.
 *
 * @see ContainerPolicy
 * @author Big Country
 * @since TOPLink/Java 2.5
 */
public class IndirectListContainerPolicy extends ListContainerPolicy {

    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public IndirectListContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public IndirectListContainerPolicy(Class containerClass) {
        super(containerClass);
    }


    /**
     * INTERNAL:
     * Return a clone of the specified container.
     */
    public Object cloneFor(Object container) {
        if (container == null) {
            return null;
        }
        // Use Vector as new objects can have a Vector.
        try {
            return ((Vector)container).clone();
        } catch (Exception notVector) {
            // Could potentially be another Collection type as well.
            return new IndirectList((Collection)container);
        }
    }
    
    /**
     * INTERNAL:
     * Just return the Vector.
     */
    public Object buildContainerFromVector(Vector vector, AbstractSession session) {
        return new IndirectList(vector);
    }
    
    /**
     * INTERNAL:
     * Return a new Vector.
     */
    public Object containerInstance() {
        return new IndirectList();
    }
    
    /**
     * INTERNAL:
     * Return a new Vector.
     */
    public Object containerInstance(int initialCapacity) {
        return new IndirectList(initialCapacity);
    }
}

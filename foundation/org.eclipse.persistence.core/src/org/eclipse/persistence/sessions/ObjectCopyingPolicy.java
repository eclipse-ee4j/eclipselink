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
package org.eclipse.persistence.sessions;

import java.util.*;

/**
 * <b>Purpose</b>: Define how an object is to be copied.<p>
 * <b>Description</b>: This is for usage with the object copying feature, not the unit of work.
 *                     This is useful for copying an entire object graph as part of the
 *                     host application's logic.<p>
 * <b>Responsibilities</b>:<ul>
 * <li> Indicate through CASCADE levels the depth relationships will copied.
 * <li> Indicate if PK attributes should be copied with existing value or should be reset.
 * </ul>
 * @since TOPLink/Java 3.0
 * @see Session#copyObject(Object, ObjectCopyingPolicy)
 * @deprecated use CopyGroup instead.
 */
public class ObjectCopyingPolicy extends CopyGroup {
    /**
     * PUBLIC:
     * Return a new copying policy.
     * By default the policy cascades privately owned parts and nulls primary keys.
     */
    public ObjectCopyingPolicy() {
        super();
        this.shouldResetPrimaryKey = true;
        // 2612538 - the default size of Map (32) is appropriate
        this.copies = new IdentityHashMap();
        this.depth = CASCADE_PRIVATE_PARTS;
    }
}

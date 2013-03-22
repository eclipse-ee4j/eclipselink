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
package org.eclipse.persistence.sessions.changesets;

import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: To provide API into the EISCollectionChangeSet.
 * <p>
 * <b>Description</b>: Capture the changes for an unordered collection as
 * collections of adds and removes.
 * <p>
 */
public interface EISCollectionChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Return the objects added to the collection.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getAdds();

    /**
     * <p>
     * ADVANCED:
     * Return the objets whose Map keys have changed.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getChangedMapKeys();

    /**
     * ADVANCED:
     * Return the removed objects.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getRemoves();

    /**
     * ADVANCED:
     * Return whether any changes have been recorded with the change record.
     */
    public boolean hasChanges();
}

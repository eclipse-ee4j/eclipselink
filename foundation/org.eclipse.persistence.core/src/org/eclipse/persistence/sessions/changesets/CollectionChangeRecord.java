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

import java.util.*;

/**
 * <p>
 * <b>Purpose</b>: This interface defines the API for the changeRecord that maintains the changes made to a collection attribute of
 * an object.
 * <p>
 * <b>Description</b>: The CollectionChangeRecord stores a list of objects removed from the collection and a separate list of objects
 * added to a collection
 */
public interface CollectionChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * This method returns the Map that contains the added values to the collection
     * and their corresponding ChangeSets.
     * @return java.util.Vector
     */
    public Map getAddObjectList();

    /**
     * ADVANCED:
     * This method returns the Map that contains the removed values from the collection
     * and their corresponding ChangeSets.
     * @return java.util.Vector
     */
    public Map getRemoveObjectList();

    /**
     * ADVANCED:
     * This method returns true if the change set has changes
     * @return boolean
     */
    public boolean hasChanges();
}

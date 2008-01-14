/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;

/**
 * <p>
 * <b>Purpose</b>: This class holds the record of the changes made to a collection attribute of
 * an object.
 * <p>
 * <b>Description</b>: Collections must be compared to each other and added and removed objects must
 * be recorded separately.
 */
public class OrderedDirectCollectionChangeRecord extends OrderedCollectionChangeRecord {

    /**
     * INTERNAL:
     * This constructor returns a changeRecord representing the DirectCollection mapping
     */
    public OrderedDirectCollectionChangeRecord(ObjectChangeSet owner) {
        super(owner);
    }

    /**
     * INTERNAL:
     * This method takes a hastable of primitive objects and adds them to the add list.
     */
    public void addAdditionChange(Hashtable additions, Vector indexes, UnitOfWorkChangeSet changes, AbstractSession session) {
        this.addObjectList = additions;
        this.addIndexes = indexes;
    }
}
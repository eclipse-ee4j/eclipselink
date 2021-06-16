/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sessions.changesets;

import java.util.*;

/**
 * <p>
 * <b>Purpose</b>: To Provide API to the UnitOfWorkChangeSet.
 * <p>
 * <b>Description</b>:The UnitOfWorkChangeSet contains all of the individual ObjectChangeSets.  It is stored and used by the UnitOfWork
 */
public interface UnitOfWorkChangeSet {

    /**
     * ADVANCED:
     * This method returns a reference to the collection.  Not All ChangeSets that Exist in this list may have changes
     * @return Map
     */
    Map getAllChangeSets();

    /**
     * ADVANCED:
     * This method returns the reference to the deleted objects from the changeSet
     * @return Map
     */
    Map getDeletedObjects();

    /**
     * ADVANCED:
     * Get ChangeSet for a particular clone
     * @return org.eclipse.persistence.sessions.changesets.ObjectChangeSet the changeSet that represents a particular clone
     */
    ObjectChangeSet getObjectChangeSetForClone(Object clone);

    /**
     * ADVANCED:
     * This method returns the Clone for a particular changeSet
     * @return Object the clone represented by the changeSet
     */
    Object getUOWCloneForObjectChangeSet(ObjectChangeSet changeSet);

    /**
     * ADVANCED:
     * Returns true if the Unit Of Work change Set has changes
     * @return boolean
     */
    boolean hasChanges();
}

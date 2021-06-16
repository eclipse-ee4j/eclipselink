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
    Map getAddObjectList();

    /**
     * ADVANCED:
     * This method returns the Map that contains the removed values from the collection
     * and their corresponding ChangeSets.
     * @return java.util.Vector
     */
    Map getRemoveObjectList();

    /**
     * ADVANCED:
     * This method returns true if the change set has changes
     * @return boolean
     */
    boolean hasChanges();
}

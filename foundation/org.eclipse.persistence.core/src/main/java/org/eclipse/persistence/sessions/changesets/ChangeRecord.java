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


/**
 * <p>
 * <b>Purpose</b>: Define the base Change Record API.
 * <p>
 * <b>Description</b>: This interface is meant to clarify the public protocol into TopLink.
 * It provides access into the information available from the TopLink Change Set
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define the API for ChangeRecord.
 * </ul>
 */
public interface ChangeRecord {

    /**
     * ADVANCED:
     * Returns the name of the attribute this ChangeRecord Represents
     * @return java.lang.String
     */
    String getAttribute();

    /**
     * ADVANCED:
     * This method returns the ObjectChangeSet that references this ChangeRecord
     * @return org.eclipse.persistence.sessions.changesets.ObjectChangeSet
     */
    ObjectChangeSet getOwner();

    /**
     * ADVANCED:
     * If the owning UnitOfWork has shouldChangeRecordKeepOldValue set to true,
     * then return the old value of the attribute represented by this ChangeRecord.
     */
    Object getOldValue();
}

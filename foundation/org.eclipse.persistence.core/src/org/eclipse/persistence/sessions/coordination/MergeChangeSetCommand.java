/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sessions.coordination;

import org.eclipse.persistence.internal.sessions.*;

/**
 * <p>
 * <b>Purpose</b>: Provide a remote command implementation for remote cache
 * merges of changes.
 * <p>
 * <b>Description</b>: This command provides the implementation for cache
 * synchronization using RCM.
 * <P>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 *
 */
public class MergeChangeSetCommand extends Command {

    /** The changes to be applied remotely */
    protected UnitOfWorkChangeSet changeSet;

    /**
     * INTERNAL:
     * Return the changes to be applied
     */
    public UnitOfWorkChangeSet getChangeSet(AbstractSession session) {
        return changeSet;
    }

    /**
     * INTERNAL:
     * Set the changes to be applied
     */
    public void setChangeSet(UnitOfWorkChangeSet newChangeSet) {
        changeSet = newChangeSet;
    }

    /**
     * INTERNAL:
     * This method will be invoked by the RCM only when the CommandProcessor is a
     * TopLink session. The session will be passed in for the command to use.
     */
    @Override
    public void executeWithSession(AbstractSession session) {
        MergeManager manager = new MergeManager(session);
        manager.mergeIntoDistributedCache();
        manager.setCascadePolicy(MergeManager.CASCADE_ALL_PARTS);

        // Do the main merge
        manager.mergeChangesFromChangeSet(getChangeSet(session));
    }
}

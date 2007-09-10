/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.remote;

import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: This class provides an implementation of a RemoteSessionCommand
 * <p>
 * <b>Description</b>: This command provides the implementation for cache synchronization messages
 * <p>
 */
public class SynchronizeCacheCommand implements RemoteCommand {

    /** This attribute holds the UnitOfWorkChangeSet that will be applied to the remote session */
    protected UnitOfWorkChangeSet changeSet;

    /** This attribute stores the unique ID of the source Session */
    protected String sourceSessionId;

    /**
     * INTERNAL:
     * Use this method to set the ChangeSet into the command
     */
    public UnitOfWorkChangeSet getChangeSet() {
        return this.changeSet;
    }

    /**
     * INTERNAL:
     * Use this method to set the ChangeSet into the command
     */
    public void setChangeSet(UnitOfWorkChangeSet changeSet) {
        this.changeSet = changeSet;
    }

    /**
     * INTERNAL:
     * Returns the Session Id of the sending session
     */
    public String getSourceSessionId() {
        return this.sourceSessionId;
    }

    /**
     * INTERNAL:
     * Used to set the source Session Id
     */
    public void setSourceSessionId(String sourceSessionId) {
        this.sourceSessionId = sourceSessionId;
    }

    /**
     * INTERNAL:
     * This method is used bye the remote Session to execute the command
     */
    public void execute(AbstractSession session, RemoteSessionController remoteSessionController) {
        // only if the session that sent the change set is different from this session should the command be processed
        if (!session.getCacheSynchronizationManager().getClusteringService().getSessionId().equals(getSourceSessionId())) {
            MergeManager manager = new MergeManager(session);
            manager.mergeIntoDistributedCache();
            manager.setCascadePolicy(MergeManager.CASCADE_ALL_PARTS);
            manager.mergeChangesFromChangeSet(getChangeSet());
            session.log(SessionLog.FINEST, SessionLog.PROPAGATION, "applying_changeset_from_remote_server", getSourceSessionId());
        }
    }
}
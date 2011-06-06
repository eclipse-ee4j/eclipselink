/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.returning;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.testing.framework.*;

/**
 * This adapter is meant to generate INSERT and UPDATE
 * procedures that return values, but currently only generates
 * procedures that just perform the INSERT/UPDATE, so is not currently used.
 * It will hopefully be finished at some point...
 */
public class InsertUpdateStoredProcedureAdapter implements ProjectAndDatabaseAdapter {

    StoredProcedureGeneratorForAdapter generator;

    public boolean isOriginalSetupRequired() {
        return true;
    }

    public void updateProject(Project project, Session session) {
        verifySession(session);
        if (generator == null) {
            generator = new StoredProcedureGeneratorForAdapter(new SchemaManager((DatabaseSession)session));
            generator.setUseTableNames(true);
        }
        removeOptimisticLocking(project);
        ((DatabaseSession)session).addDescriptors(project);
        generator.generateStoredProceduresDefinitionsForProject(project);
        generator.amendDescriptors();
    }

    public void updateDatabase(Session session) {
        try {
            generator.writeStoredProcedures();
        } finally {
            clear();
        }
    }

    protected void clear() {
        generator = null;
    }

    // StoredProcedureGenerator currently can't handle optimistic locking

    protected int removeOptimisticLocking(Project project) {
        int removed = 0;
        Iterator descriptors = project.getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor desc = (ClassDescriptor)descriptors.next();
            if (desc.getOptimisticLockingPolicy() != null) {
                desc.setOptimisticLockingPolicy(null);
                removed++;
            }
        }
        return removed;
    }

    protected void verifySession(Session session) {
        DatabasePlatform platform = session.getPlatform();
        if (((DatabaseSessionImpl)session).getSequencing() != null) {
            if (((DatabaseSessionImpl)session).getSequencing().whenShouldAcquireValueForAll() != Sequencing.BEFORE_INSERT) {
                // On Sybase, Infomix, SQLServer native sequencing is provided by calling @@IDENTITY after insert
                // Apparently if INSERT has occurred inside stored procedure, SELECT @@IDENTITY returns 0.
                throw new TestWarningException("InsertUpdateStoredProcedureAdapter can't handle native sequencing on this platform. Use table sequencing instead");
            }
        }
        if ((platform.isSybase() || platform.isSQLAnywhere()) && platform.supportsAutoCommit()) {
            // stored procedures won't work unless
            // the flag set to false (at least with JConnect).
            // see DatabaseLogin.handleTransactionsManuallyForSybaseJConnect()
            throw new TestWarningException("Sybase requires supportAutoCommit()==false");
        }
    }

}

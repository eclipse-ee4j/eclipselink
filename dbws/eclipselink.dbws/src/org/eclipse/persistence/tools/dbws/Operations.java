/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports

// Java extension imports

// EclipseLink imports

// Ant imports
import java.util.ArrayList;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class Operations extends Task implements TaskContainer {

    protected ArrayList<Op> operations = new ArrayList<Op>();

    public void addTask(Task task) {
    } // ignore

    public void addConfiguredSQLOperation(SQLOperation sqlOperation) {
        operations.add(sqlOperation);
    }

    public void addConfiguredProcedure(Procedure procedure) {
        operations.add(procedure);
    }

    public ArrayList<Op> getOperations() {
        return operations;
    }
}

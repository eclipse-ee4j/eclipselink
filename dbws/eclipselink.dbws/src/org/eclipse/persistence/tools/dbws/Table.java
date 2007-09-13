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

public class Table extends Task implements TaskContainer {

    protected String catalogPattern;
    protected String schemaPattern;
    protected String tableNamePattern;
    protected Operations operations;

    public Table() {
        super();
    }

    public void addTask(Task task) {
    } // ignore

    public String getCatalogPattern() {
        return catalogPattern;
    }
    public void setCatalogPattern(String catalogPattern) {
        this.catalogPattern = catalogPattern;
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }
    public void setSchemaPattern(String schemaPattern) {
        this.schemaPattern = schemaPattern;
    }

    public String getTableNamePattern() {
        return tableNamePattern;
    }
    public void setTableNamePattern(String tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }

    public void addConfiguredOperations(Operations operations) {
        this.operations = operations;
    }

    public ArrayList<Op> getOperations() {
        return operations.getOperations();
    }

}

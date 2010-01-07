/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Define a foreign key from one table to another.
 * This support composite foreign keys can constraint options.
 */
public class ForeignKeyConstraint implements Serializable {
    protected String name;
    protected Vector sourceFields;
    protected Vector targetFields;
    protected String targetTable;
    protected boolean shouldCascadeOnDelete;

    public ForeignKeyConstraint() {
        this.name = "";
        this.sourceFields = new Vector();
        this.targetFields = new Vector();
        this.targetTable = "";
        this.shouldCascadeOnDelete = false;
    }

    public ForeignKeyConstraint(String name, String sourceField, String targetField, String targetTable) {
        this();
        this.name = name;
        sourceFields.addElement(sourceField);
        targetFields.addElement(targetField);
        this.targetTable = targetTable;
    }

    public void addSourceField(String sourceField) {
        getSourceFields().addElement(sourceField);
    }

    public void addTargetField(String targetField) {
        getTargetFields().addElement(targetField);
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            writer.write("FOREIGN KEY (");
            for (Enumeration sourceEnum = getSourceFields().elements();
                     sourceEnum.hasMoreElements();) {
                writer.write((String)sourceEnum.nextElement());
                if (sourceEnum.hasMoreElements()) {
                    writer.write(", ");
                }
            }
            writer.write(") REFERENCES ");
            writer.write(getTargetTable());
            writer.write(" (");
            for (Enumeration targetEnum = getTargetFields().elements();
                     targetEnum.hasMoreElements();) {
                writer.write((String)targetEnum.nextElement());
                if (targetEnum.hasMoreElements()) {
                    writer.write(", ");
                }
            }
            writer.write(")");
            if (shouldCascadeOnDelete()) {
                writer.write(" ON DELETE CASCADE");
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * PUBLIC:
     * Enables delete cascading on the database.
     * This must be used carefully, i.e. only private relationships.
     */
    public void cascadeOnDelete() {
        setShouldCascadeOnDelete(true);
    }

    /**
     * PUBLIC:
     * Disables delete cascading on the database, this is the default.
     */
    public void dontCascadeOnDelete() {
        setShouldCascadeOnDelete(false);
    }

    public String getName() {
        return name;
    }

    public Vector getSourceFields() {
        return sourceFields;
    }

    public Vector getTargetFields() {
        return targetFields;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * PUBLIC:
     * Enables delete cascading on the database.
     * This must be used carefully, i.e. only private relationships.
     */
    public void setShouldCascadeOnDelete(boolean shouldCascadeOnDelete) {
        this.shouldCascadeOnDelete = shouldCascadeOnDelete;
    }

    public void setSourceFields(Vector sourceFields) {
        this.sourceFields = sourceFields;
    }

    public void setTargetFields(Vector targetFields) {
        this.targetFields = targetFields;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public boolean shouldCascadeOnDelete() {
        return shouldCascadeOnDelete;
    }
}

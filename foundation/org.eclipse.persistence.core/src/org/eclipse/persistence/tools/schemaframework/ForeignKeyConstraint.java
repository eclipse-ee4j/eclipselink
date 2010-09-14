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
    protected List<String> sourceFields;
    protected List<String> targetFields;
    protected String targetTable;
    protected boolean shouldCascadeOnDelete;

    public ForeignKeyConstraint() {
        this.name = "";
        this.sourceFields = new ArrayList();
        this.targetFields = new ArrayList();
        this.targetTable = "";
        this.shouldCascadeOnDelete = false;
    }

    public ForeignKeyConstraint(String name, String sourceField, String targetField, String targetTable) {
        this();
        this.name = name;
        sourceFields.add(sourceField);
        targetFields.add(targetField);
        this.targetTable = targetTable;
    }

    public void addSourceField(String sourceField) {
        getSourceFields().add(sourceField);
    }

    public void addTargetField(String targetField) {
        getTargetFields().add(targetField);
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            writer.write("FOREIGN KEY (");
            for (Iterator iterator = getSourceFields().iterator(); iterator.hasNext();) {
                writer.write((String)iterator.next());
                if (iterator.hasNext()) {
                    writer.write(", ");
                }
            }
            writer.write(") REFERENCES ");
            writer.write(getTargetTable());
            writer.write(" (");
            for (Iterator iterator = getTargetFields().iterator(); iterator.hasNext();) {
                writer.write((String)iterator.next());
                if (iterator.hasNext()) {
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

    public List<String> getSourceFields() {
        return sourceFields;
    }

    public List<String> getTargetFields() {
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

    public void setSourceFields(List<String> sourceFields) {
        this.sourceFields = sourceFields;
    }

    public void setTargetFields(List<String> targetFields) {
        this.targetFields = targetFields;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public boolean shouldCascadeOnDelete() {
        return shouldCascadeOnDelete;
    }
}

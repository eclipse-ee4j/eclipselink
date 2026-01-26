/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Define a foreign key from one table to another.
 * This support composite foreign keys can constraint options.
 */
public class ForeignKeyConstraint extends KeyConstraintObjectDefinition {
    protected List<String> targetFields;
    protected String targetTable;
    protected boolean shouldCascadeOnDelete;

    protected String foreignKeyDefinition;
    protected boolean disableForeignKey;

    public ForeignKeyConstraint() {
        super();
        this.targetFields = new ArrayList<>();
        this.targetTable = "";
        this.shouldCascadeOnDelete = false;
    }

    public ForeignKeyConstraint(String name, String sourceField, String targetField, String targetTable) {
        super(name, sourceField);
        targetFields = new ArrayList<>();
        targetFields.add(targetField);
        this.targetTable = targetTable;
    }

    public void addTargetField(String targetField) {
        getTargetFields().add(targetField);
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            if (hasForeignKeyDefinition()) {
                writer.write(getForeignKeyDefinition());
            } else {
                writer.write("FOREIGN KEY ");
                appendKeys(writer, getSourceFields());
                writer.write(" REFERENCES ");
                writer.write(getTargetTable());
                writer.write(" ");
                appendKeys(writer, getTargetFields());
                if (shouldCascadeOnDelete() && session.getPlatform().supportsDeleteOnCascade()) {
                    writer.write(" ON DELETE CASCADE");
                }
                super.appendDBString(writer, session);
            }
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof IOException) {
                throw ValidationException.fileError((IOException) ex.getCause());
            }
            throw ValidationException.fileError((new IOException(ex.getCause())));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * Enables delete cascading on the database.
     * This must be used carefully, i.e. only private relationships.
     */
    public void cascadeOnDelete() {
        setShouldCascadeOnDelete(true);
    }

    public boolean disableForeignKey() {
        return this.disableForeignKey;
    }

    /**
     * Disables delete cascading on the database, this is the default.
     */
    public void dontCascadeOnDelete() {
        setShouldCascadeOnDelete(false);
    }

    public String getForeignKeyDefinition() {
        return foreignKeyDefinition;
    }

    public List<String> getTargetFields() {
        return targetFields;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public boolean hasForeignKeyDefinition() {
        return foreignKeyDefinition != null;
    }

    public boolean isDisableForeignKey() {
        return disableForeignKey;
    }

    public void setDisableForeignKey(boolean disableForeignKey) {
        this.disableForeignKey = disableForeignKey;
    }

    public void setForeignKeyDefinition(String foreignKeyDefinition) {
        this.foreignKeyDefinition = foreignKeyDefinition;
    }

    /**
     * Enables delete cascading on the database.
     * This must be used carefully, i.e. only private relationships.
     */
    public void setShouldCascadeOnDelete(boolean shouldCascadeOnDelete) {
        this.shouldCascadeOnDelete = shouldCascadeOnDelete;
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

/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.tools.schemaframework;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow for indexes to be created.
 * <p>
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class IndexDefinition extends DatabaseObjectDefinition {
    protected String targetTable;
    protected List<String> fields;
    protected boolean isUnique;

    public IndexDefinition() {
        this.fields = new ArrayList();
    }
    
    public boolean isUnique() {
        return isUnique;
    }

    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public String getTargetTable() {
        return targetTable;
    }

    /**
     * PUBLIC:
     * set qualified table name.
     */
    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    /**
     * PUBLIC:
     * Add the field to the index.
     */
    public void addField(String fieldName) {
        this.fields.add(fieldName);
    }

    /**
     * INTERNAL:
     * Return the create type statement.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write(session.getPlatform().buildCreateIndex(getTargetTable(), getName(), getQualifier(), isUnique(), this.fields.toArray(new String[0])));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the drop type statement.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write(session.getPlatform().buildDropIndex(getTargetTable(), getName(), getQualifier()));
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}

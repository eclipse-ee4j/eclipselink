/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
 * <b>Purpose</b>: Define a unique key constraint for a table.
 */
public class UniqueKeyConstraint implements Serializable {
    protected String name;
    protected Vector<String> sourceFields; // field names

    public UniqueKeyConstraint() {
        this.name = "";
        this.sourceFields = new Vector<String>();
    }

    public UniqueKeyConstraint(String name, String sourceField) {
        this();
        this.name = name;
        sourceFields.addElement(sourceField);
    }

    public UniqueKeyConstraint(String name, String[] sourceFields) {
        this();
        this.name = name;
        for(String sourceField : sourceFields) {
            this.sourceFields.addElement(sourceField);
        }
    }
    
    /**
     * PUBLIC:
     */
    public void addSourceField(String sourceField) {
        getSourceFields().addElement(sourceField);
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     */
    public void appendDBString(Writer writer, AbstractSession session) {
        try {
            writer.write("UNIQUE (");
            for (Enumeration sourceEnum = getSourceFields().elements();
                     sourceEnum.hasMoreElements();) {
                writer.write((String)sourceEnum.nextElement());
                if (sourceEnum.hasMoreElements()) {
                    writer.write(", ");
                }
            }
            writer.write(")");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * PUBLIC:
     */
    public String getName() {
        return name;
    }

    /**
     * PUBLIC:
     */
    public Vector<String> getSourceFields() {
        return sourceFields;
    }

    /**
     * PUBLIC:
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * PUBLIC:
     */
    public void setSourceFields(Vector<String> sourceFields) {
        this.sourceFields = sourceFields;
    }
}


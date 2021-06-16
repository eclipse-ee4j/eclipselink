/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.schemaframework;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow for Oracle 8 object-relational user defined type to be created.
 * </p>
 */
public class TypeDefinition extends DatabaseObjectDefinition {
    protected Vector fields;

    public TypeDefinition() {
        this.fields = new Vector();
    }

    /**
     * PUBLIC:
     * Add the field to the type, default sizes are used.
     * @param type is the Java class type coresponding to the database type.
     */
    public void addField(String fieldName, Class type) {
        this.addField(new FieldDefinition(fieldName, type));
    }

    /**
     * PUBLIC:
     * Add the field to the type.
     * @param type is the Java class type coresponding to the database type.
     */
    public void addField(String fieldName, Class type, int fieldSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize));
    }

    /**
     * PUBLIC:
     * Add the field to the type.
     * @param type is the Java class type coresponding to the database type.
     */
    public void addField(String fieldName, Class type, int fieldSize, int fieldSubSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize, fieldSubSize));
    }

    /**
     * PUBLIC:
     * Add the field to the type to a nested type.
     * @param typeName is the name of the nested type.
     */
    public void addField(String fieldName, String typeName) {
        this.addField(new FieldDefinition(fieldName, typeName));
    }

    /**
     * PUBLIC:
     * Add the field to the type.
     */
    public void addField(FieldDefinition field) {
        this.getFields().addElement(field);
    }

    /**
     * INTERNAL:
     * Return the create type statement.
     */
    @Override
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("CREATE TYPE " + getFullName() + " AS OBJECT (");
            for (Enumeration fieldsEnum = getFields().elements(); fieldsEnum.hasMoreElements();) {
                FieldDefinition field = (FieldDefinition)fieldsEnum.nextElement();
                field.appendTypeString(writer, session);
                if (fieldsEnum.hasMoreElements()) {
                    writer.write(", ");
                }
            }
            writer.write(")");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the drop type statement.
     */
    @Override
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP TYPE " + getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    public Vector getFields() {
        return fields;
    }

    public void setFields(Vector fields) {
        this.fields = fields;
    }
}

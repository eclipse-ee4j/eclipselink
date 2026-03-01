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
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow for Oracle 8 object-relational user defined type to be created.
 * </p>
 */
public class TypeDefinition extends DatabaseObjectDefinition {
    protected List<FieldDefinition> fields;

    public TypeDefinition() {
        this.fields = new ArrayList<>();
    }

    /**
     * Add the field to the type, default sizes are used.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class<?> type) {
        this.addField(new FieldDefinition(fieldName, type));
    }

    /**
     * Add the field to the type.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class<?> type, int fieldSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize));
    }

    /**
     * Add the field to the type.
     * @param type is the Java class type corresponding to the database type.
     */
    public void addField(String fieldName, Class<?> type, int fieldSize, int fieldSubSize) {
        this.addField(new FieldDefinition(fieldName, type, fieldSize, fieldSubSize));
    }

    /**
     * Add the field to the type to a nested type.
     * @param typeName is the name of the nested type.
     */
    public void addField(String fieldName, String typeName) {
        this.addField(new FieldDefinition(fieldName, typeName));
    }

    /**
     * Add the field to the type.
     */
    public void addField(FieldDefinition field) {
        this.getFields().add(field);
    }

    /**
     * INTERNAL:
     * Return the create type statement.
     */
    @Override
    @Deprecated(forRemoval = true, since = "4.0.9")
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("CREATE TYPE " + getFullName() + " AS OBJECT (");
            List<FieldDefinition> fields = getFields();
            for (int i = 0; i < getFields().size(); i++) {
                FieldDefinition field = fields.get(i);
                field.appendTypeString(writer, session);
                if (i + 1 < fields.size()) {
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
    @Deprecated(forRemoval = true, since = "4.0.9")
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP TYPE " + getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }
}

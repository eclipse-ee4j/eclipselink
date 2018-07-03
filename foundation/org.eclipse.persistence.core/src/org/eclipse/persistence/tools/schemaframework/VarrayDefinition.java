/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: Allow for creation of varray type.
 * </p>
 */
public class VarrayDefinition extends DatabaseObjectDefinition {
    protected int typeSize;
    protected int size;
    protected Class type;
    protected String typeName;

    public VarrayDefinition() {
        super();
    }

    /**
     * INTERNAL:
     * Append the type.
     * @param writer   Target writer where to write type string.
     * @param session  Current session context.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    public void appendTypeString(final Writer writer, final AbstractSession session)
            throws ValidationException {
        try {
            final FieldTypeDefinition fieldType = getFieldTypeDefinition(session, type, typeName);
            writer.write(fieldType.getName());
            if ((fieldType.isSizeAllowed()) && ((typeSize != 0) || (fieldType.isSizeRequired()))) {
                writer.write("(");
                if (typeSize == 0) {
                    writer.write(Integer.toString(fieldType.getDefaultSize()));
                } else {
                    writer.write(Integer.toString(typeSize));
                }
                writer.write(")");
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * Return the DDL to create the {@code VARRAY}.
     * @param writer   Target writer.
     * @param session  Current session context.
     * @return Target writer with {@code CREATE TYPE ... AS VARRAY (...) OF ...}
     *         already written to it.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    public Writer buildCreationWriter(final AbstractSession session, final Writer writer)
            throws ValidationException {
        try {
            writer.write("CREATE TYPE ");
            writer.write(getFullName());
            writer.write(" AS VARRAY(");

            //when defining a VARRAY type, a maximum size MUST be specified
            if (size < 1) {
                throw ValidationException.oracleVarrayMaximumSizeNotDefined(getFullName());
            }

            writer.write(Integer.toString(size));
            writer.write(") OF ");
            appendTypeString(writer, session);
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the DDL to drop the varray.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP TYPE ");
            writer.write(getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * PUBLIC:
     * Return the maximum size of the array.
     */
    public int getSize() {
        return size;
    }

    /**
     * PUBLIC:
     * Return the type of the field.
     * This should be set to a java class, such as String.class, Integer.class or Date.class.
     */
    public Class getType() {
        return type;
    }

    /**
     * PUBLIC:
     * Return the type of the field.
     * This is the exact DB type name, which can be used instead of the Java class.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * PUBLIC:
     * Return the size of the element field, this is only required for some field types.
     */
    public int getTypeSize() {
        return typeSize;
    }

    /**
     * PUBLIC:
     * Set the maximum size of the array.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * PUBLIC:
     * Set the type of the field.
     * This should be set to a java class, such as String.class, Integer.class or Date.class.
     */
    public void setType(Class type) {
        this.type = type;
    }

    /**
     * PUBLIC:
     * Set the type of the field.
     * This is the exact DB type name, which can be used instead of the Java class.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * PUBLIC:
     * Set the size of the element field, this is only required for some field types.
     */
    public void setTypeSize(int typeSize) {
        this.typeSize = typeSize;
    }
}

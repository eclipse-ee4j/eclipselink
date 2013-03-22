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
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: Allow for creation of varray type.
 * <p>
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
     */
    public void appendTypeString(Writer writer, AbstractSession session) throws ValidationException {
        try {
            FieldTypeDefinition fieldType;
            if (getType() != null) {
                fieldType = session.getPlatform().getFieldTypeDefinition(getType());
                if (fieldType == null) {
                    throw ValidationException.javaTypeIsNotAValidDatabaseType(getType());
                }
            } else {
                fieldType = new FieldTypeDefinition(getTypeName());
            }
            writer.write(fieldType.getName());
            if ((fieldType.isSizeAllowed()) && ((getTypeSize() != 0) || (fieldType.isSizeRequired()))) {
                writer.write("(");
                if (getTypeSize() == 0) {
                    writer.write(Integer.valueOf(fieldType.getDefaultSize()).toString());
                } else {
                    writer.write(Integer.valueOf(getTypeSize()).toString());
                }
                writer.write(")");
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * Return the DDL to create the varray.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("CREATE TYPE ");
            writer.write(getFullName());
            writer.write(" AS VARRAY(");

            //when defining a VARRAY type, a maximum size MUST be specified
            if (getSize() < 1) {
                throw ValidationException.oracleVarrayMaximumSizeNotDefined(getFullName());
            }

            writer.write(Integer.valueOf(getSize()).toString());
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
            writer.write("DROP TYPE " + getFullName());
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

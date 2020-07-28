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

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: Define a database field definition for creation within a table.
 * This differs from DatabaseField in that it is used only table creation not a runtime.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Store the name, java type, size and sub-size.
 * The sizes are optional and the name of the java class is used for the type.
 * </ul>
 */
public class FieldDefinition implements Serializable, Cloneable {
    protected String name;
    /**
     * Java type class for the field.
     * Particular database type is generated based on platform from this.
     */
    protected Class type;
    /**
     * Generic database type name for the field, which can be used instead of the Java class 'type'.
     * This is translated to a particular database type based on platform.
     */
    protected String typeName;
    /**
     * DatabaseField stores the field name with case and delimiting information.
     * Used if the field needs to be found in the table metadata, for extending tables.
     * if null, name is used for comparison to determine if this field already exists.
     */
    protected DatabaseField field;
    /**
     * Database-specific complete type definition like "VARCHAR2(50) UNIQUE NOT NULL".
     * If this is given, other additional type constraint fields(size, unique, null) are meaningless.
     */
    protected String typeDefinition;
    protected int size;
    protected int subSize;
    protected boolean shouldAllowNull;
    protected boolean isIdentity;
    protected boolean isPrimaryKey;
    protected boolean isUnique;
    protected String additional;
    protected String constraint;
    protected String foreignKeyFieldName;

    public FieldDefinition() {
        this.name = "";
        this.size = 0;
        this.subSize = 0;
        this.shouldAllowNull = true;
        this.isIdentity = false;
        this.isPrimaryKey = false;
        this.isUnique = false;
    }

    public FieldDefinition(String name, Class type) {
        this.name = name;
        this.type = type;
        this.size = 0;
        this.subSize = 0;
        shouldAllowNull = true;
        isIdentity = false;
        isPrimaryKey = false;
        isUnique = false;
    }

    public FieldDefinition(String name, Class type, int size) {
        this();
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public FieldDefinition(String name, Class type, int size, int subSize) {
        this();
        this.name = name;
        this.type = type;
        this.size = size;
        this.subSize = subSize;
    }

    public FieldDefinition(String name, String typeName) {
        this();
        this.name = name;
        this.typeName = typeName;
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation statement.
     * @param writer  Target writer where to write field definition string.
     * @param session Current session context.
     * @param table   Database table being processed.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    public void appendDBString(final Writer writer, final AbstractSession session,
            final TableDefinition table) throws ValidationException {
        try {
            writer.write(name);
            writer.write(" ");

            if (getTypeDefinition() != null) { //apply user-defined complete type definition
                writer.write(typeDefinition);

            } else {
                final DatabasePlatform platform = session.getPlatform();
                // compose type definition - type name, size, unique, identity, constraints...
                final FieldTypeDefinition fieldType
                        = DatabaseObjectDefinition.getFieldTypeDefinition(platform, type, typeName);

                String qualifiedName = table.getFullName() + '.' + name;
                boolean shouldPrintFieldIdentityClause = isIdentity && platform.shouldPrintFieldIdentityClause(session, qualifiedName);
                platform.printFieldTypeSize(writer, this, fieldType, shouldPrintFieldIdentityClause);

                if (shouldPrintFieldIdentityClause) {
                    platform.printFieldIdentityClause(writer);
                }
                if (shouldAllowNull && fieldType.shouldAllowNull()) {
                    platform.printFieldNullClause(writer);
                } else {
                    platform.printFieldNotNullClause(writer);
                }
                if (isUnique) {
                    if (platform.supportsUniqueColumns()) {
                        // #282751: do not add UNIQUE if the field is also simple primary key
                        if (!isPrimaryKey || table.getPrimaryKeyFieldNames().size() > 1) {
                            platform.printFieldUnique(writer, shouldPrintFieldIdentityClause);
                        } else {
                            setUnique(false);
                            session.log(SessionLog.WARNING, SessionLog.DDL, "removing_unique_constraint", qualifiedName);
                        }
                    } else {
                        // Need to move the unique column to be a constraint.
                        setUnique(false);
                        String constraintName = table.buildUniqueKeyConstraintName(table.getName(), table.getFields().indexOf(this), platform.getMaxUniqueKeyNameSize());
                        table.addUniqueKeyConstraint(constraintName, name);
                    }
                }
                if (constraint != null) {
                    writer.write(" " + constraint);
                }
                if (additional != null) {
                    writer.write(" " + additional);
                }
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the type creation statement.
     * Types do not support constraints.
     * @param writer  Target writer where to write field definition string.
     * @param session Current session context.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    public void appendTypeString(final Writer writer, final AbstractSession session)
            throws ValidationException {
        final FieldTypeDefinition fieldType
                = DatabaseObjectDefinition.getFieldTypeDefinition(session, type, typeName);
        try {
            writer.write(name);
            writer.write(" ");
            writer.write(fieldType.getName());
            if ((fieldType.isSizeAllowed()) && ((size != 0) || (fieldType.isSizeRequired()))) {
                writer.write("(");
                if (size == 0) {
                    writer.write(Integer.toString(fieldType.getDefaultSize()));
                } else {
                    writer.write(Integer.toString(size));
                }
                if (subSize != 0) {
                    writer.write(",");
                    writer.write(Integer.toString(subSize));
                } else if (fieldType.getDefaultSubSize() != 0) {
                    writer.write(",");
                    writer.write(Integer.toString(fieldType.getDefaultSubSize()));
                }
                writer.write(")");
            }
            if (additional != null) {
                writer.write(" " + additional);
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * PUBLIC:
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException impossible) {
            return null;
        }
    }

    /**
     * PUBLIC:
     * Return any additional information about this field to be given when the table is created.
     */
    public String getAdditional() {
        return additional;
    }

    /**
     * PUBLIC:
     * Return any constraint of this field.
     * i.e. "BETWEEN 0 AND 1000000".
     */
    public String getConstraint() {
        return constraint;
    }

    public String getForeignKeyFieldName() {
        return foreignKeyFieldName;
    }

    /**
     * PUBLIC:
     * Return the name of the field.
     */
    public String getName() {
        return name;
    }

    /**
     * INTERNAL:
     * Return the databasefield.
     */
    public DatabaseField getDatabaseField() {
        return field;
    }

    /**
     * PUBLIC:
     * Return the size of the field, this is only required for some field types.
     */
    public int getSize() {
        return size;
    }

    /**
     * PUBLIC:
     * Return the sub-size of the field.
     * This is used as the decimal precision for numeric values only.
     */
    public int getSubSize() {
        return subSize;
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
     * Return the type name of the field.
     * This is the generic database type name, which can be used instead of the Java class 'type'.
     * This is translated to a particular database type based on platform.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * PUBLIC:
     * Return the type definition of the field.
     * This is database-specific complete type definition like "VARCHAR2(50) UNIQUE NOT NULL".
     * If this is given, other additional type constraint fields(size, unique, null) are meaningless.
     */
    public String getTypeDefinition() {
        return typeDefinition;
    }

    /**
     * PUBLIC:
     * Answer whether the receiver is an identity field.
     * Identity fields are Sybase specific,
     * they insure that on insert a unique sequential value is stored in the row.
     */
    public boolean isIdentity() {
        return isIdentity;
    }

    /**
     * PUBLIC:
     * Answer whether the receiver is a primary key.
     * If the table has a multipart primary key this should be set in each field.
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * PUBLIC:
     * Answer whether the receiver is a unique constraint field.
     */
    public boolean isUnique() {
        return isUnique;
    }

    /**
     * PUBLIC:
     * Set any additional information about this field to be given when the table is created.
     */
    public void setAdditional(String string) {
        additional = string;
    }

    /**
     * PUBLIC:
     * Set any constraint of this field.
     * i.e. "BETWEEN 0 AND 1000000".
     */
    public void setConstraint(String string) {
        constraint = string;
    }

    public void setForeignKeyFieldName(String foreignKeyFieldName) {
        this.foreignKeyFieldName = foreignKeyFieldName;
    }

    /**
     * PUBLIC:
     * Set whether the receiver is an identity field.
     * Identity fields are Sybase specific,
     * they insure that on insert a unique sequential value is stored in the row.
     */
    public void setIsIdentity(boolean value) {
        isIdentity = value;
        if (value) {
            setShouldAllowNull(false);
        }
    }

    /**
     * PUBLIC:
     * Set whether the receiver is a primary key.
     * If the table has a multipart primary key this should be set in each field.
     */
    public void setIsPrimaryKey(boolean value) {
        isPrimaryKey = value;
        if (value) {
            setShouldAllowNull(false);
        }
    }

    /**
     * PUBLIC:
     * Set the name of the field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * INTERNAL:
     * Set the DatabaseField that is associated to this FieldDefinition object.
     * The databaesField is used when extending tables to see if this field already exists.
     */
    public void setDatabaseField(DatabaseField field) {
        this.field = field;
    }

    /**
     * PUBLIC:
     * Set whether the receiver should allow null values.
     */
    public void setShouldAllowNull(boolean value) {
        shouldAllowNull = value;
    }

    /**
     * PUBLIC:
     * Set the size of the field, this is only required for some field types.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * PUBLIC:
     * Set the sub-size of the field.
     * This is used as the decimal precision for numeric values only.
     */
    public void setSubSize(int subSize) {
        this.subSize = subSize;
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
     * Set the type name of the field.
     * This is the generic database type name, which can be used instead of the Java class 'type'.
     * This is translated to a particular database type based on platform.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * PUBLIC:
     * Set the type definition of the field.
     * This is database-specific complete type definition like "VARCHAR2(50) UNIQUE NOT NULL".
     * If this is given, other additional type constraint fields(size, unique, null) are meaningless.
     */
    public void setTypeDefinition(String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    /**
     * PUBLIC:
     * Set whether the receiver is a unique constraint field.
     */
    public void setUnique(boolean value) {
        isUnique = value;
    }

    /**
     * PUBLIC:
     * Return whether the receiver should allow null values.
     */
    public boolean shouldAllowNull() {
        return shouldAllowNull;
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getName() + "(" + getType() + "))";
    }
}

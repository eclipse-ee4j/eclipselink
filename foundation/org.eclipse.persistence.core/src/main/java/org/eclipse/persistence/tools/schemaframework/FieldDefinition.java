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
//     12/05/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.tools.schemaframework;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Objects;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;

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
    protected Class<?> type;
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
    protected String comment;

    public FieldDefinition() {
        this("", (Class<?>) null);
    }

    public FieldDefinition(String name, Class<?> type) {
        this.name = name;
        this.type = type;
        this.size = 0;
        this.subSize = 0;
        shouldAllowNull = true;
        isIdentity = false;
        isPrimaryKey = false;
        isUnique = false;
    }

    public FieldDefinition(String name, Class<?> type, int size) {
        this();
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public FieldDefinition(String name, Class<?> type, int size, int subSize) {
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
     *
     * @param writer  Target writer where to write field definition string.
     * @param session Current session context.
     * @param table   Database table being processed.
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendDBString(final Writer writer, final AbstractSession session,
            final TableDefinition table) throws ValidationException {
        appendDBString(writer, session, table, null);
    }

    /**
     * INTERNAL:
     * Append the database field definition string to the table creation/modification statement.
     *
     * @param writer  Target writer where to write field definition string.
     * @param session Current session context.
     * @param table   Database table being processed.
     * @param alterKeyword Field definition is part of ALTER/MODIFY COLUMN statement
     *                and {@code alterKeyword} is appended after column name when not {@code null}
     * @throws ValidationException When invalid or inconsistent data were found.
     */
    private void appendDBString(final Writer writer, final AbstractSession session,
                                      final TableDefinition table, String alterKeyword) throws ValidationException {
        try {
            writer.write(name);
            writer.write(" ");

            // e.g. "ALTER TABLE assets ALTER COLUMN location TYPE VARCHAR" to add "TYPE" keyword
            if (alterKeyword != null) {
                writer.write(alterKeyword);
                writer.write(" ");
            }

            if (getTypeDefinition() != null) { //apply user-defined complete type definition
                writer.write(typeDefinition);

            } else {
                final DDLPlatform platform = session.getPlatform();
                // compose type definition - type name, size, unique, identity, constraints...
                final DatabaseType fieldType
                        = platform.getDatabaseType(type, typeName);

                String qualifiedName = table.getFullName() + '.' + name;
                boolean shouldPrintFieldIdentityClause = isIdentity && ((DatabasePlatform) platform).shouldPrintFieldIdentityClause(session, qualifiedName);
                platform.printFieldTypeSize(writer, this, fieldType, shouldPrintFieldIdentityClause);

                if (shouldPrintFieldIdentityClause) {
                    platform.printFieldIdentityClause(writer);
                }
                if (shouldAllowNull && fieldType.allowNull()) {
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
                        String constraintName = FrameworkHelper.buildConstraintName(table.getName(), String.valueOf(table.getFields().indexOf(this)), "UNQ_",
                                platform.getStartDelimiter(), platform.getEndDelimiter(), platform.getMaxUniqueKeyNameSize());
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
            if (comment != null) {
                writer.write(" /* ");
                writer.write(comment);
                writer.write(" */ ");
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
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void appendTypeString(final Writer writer, final AbstractSession session)
            throws ValidationException {
        final DDLPlatform platform = session.getPlatform();
        final DatabaseType fieldType
                = platform.getDatabaseType(type, typeName);
        try {
            writer.write(name);
            writer.write(" ");
            platform.printFieldTypeSize(writer, this, fieldType);
            if (additional != null) {
                writer.write(" " + additional);
            }
            if (comment != null) {
                writer.write(" /* ");
                writer.write(comment);
                writer.write(" */ ");
            }
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException impossible) {
            return null;
        }
    }

    /**
     * Return any additional information about this field to be given when the table is created.
     */
    public String getAdditional() {
        return additional;
    }

    public String getComment() {
        return comment;
    }

    /**
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
     * Return the name of the field.
     */
    public String getName() {
        return name;
    }

    /**
     * INTERNAL:
     * Return the DatabaseField.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public DatabaseField getDatabaseField() {
        return field;
    }

    /**
     * Return the size of the field, this is only required for some field types.
     */
    public int getSize() {
        return size;
    }

    /**
     * Return the sub-size of the field.
     * This is used as the decimal precision for numeric values only.
     */
    public int getSubSize() {
        return subSize;
    }

    /**
     * Return the type of the field.
     * This should be set to a java class, such as String.class, Integer.class or Date.class.
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Return the type name of the field.
     * This is the generic database type name, which can be used instead of the Java class 'type'.
     * This is translated to a particular database type based on platform.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Return the type definition of the field.
     * This is database-specific complete type definition like "VARCHAR2(50) UNIQUE NOT NULL".
     * If this is given, other additional type constraint fields(size, unique, null) are meaningless.
     */
    public String getTypeDefinition() {
        return typeDefinition;
    }

    /**
     * Answer whether the receiver is an identity field.
     * Identity fields are Sybase specific,
     * they ensure that on insert a unique sequential value is stored in the row.
     */
    public boolean isIdentity() {
        return isIdentity;
    }

    /**
     * Answer whether the receiver is a primary key.
     * If the table has a multipart primary key this should be set in each field.
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * Answer whether the receiver is a unique constraint field.
     */
    public boolean isUnique() {
        return isUnique;
    }

    /**
     * Set any additional information about this field to be given when the table is created.
     */
    public void setAdditional(String string) {
        additional = string;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
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
     * Set whether the receiver is an identity field.
     * Identity fields are Sybase specific,
     * they ensure that on insert a unique sequential value is stored in the row.
     */
    public void setIsIdentity(boolean value) {
        isIdentity = value;
        if (value) {
            setShouldAllowNull(false);
        }
    }

    /**
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
     * Set the name of the field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * INTERNAL:
     * Set the DatabaseField that is associated to this FieldDefinition object.
     * The DatabaseField is used when extending tables to see if this field already exists.
     */
    @Deprecated(forRemoval = true, since = "4.0.9")
    public void setDatabaseField(DatabaseField field) {
        this.field = field;
    }

    /**
     * Set whether the receiver should allow null values.
     */
    public void setShouldAllowNull(boolean value) {
        shouldAllowNull = value;
    }

    /**
     * Set the size of the field, this is only required for some field types.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Set the sub-size of the field.
     * This is used as the decimal precision for numeric values only.
     */
    public void setSubSize(int subSize) {
        this.subSize = subSize;
    }

    /**
     * Set the type of the field.
     * This should be set to a java class, such as String.class, Integer.class or Date.class.
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * Set the type name of the field.
     * This is the generic database type name, which can be used instead of the Java class 'type'.
     * This is translated to a particular database type based on platform.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Set the type definition of the field.
     * This is database-specific complete type definition like "VARCHAR2(50) UNIQUE NOT NULL".
     * If this is given, other additional type constraint fields(size, unique, null) are meaningless.
     */
    public void setTypeDefinition(String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    /**
     * Set whether the receiver is a unique constraint field.
     */
    public void setUnique(boolean value) {
        isUnique = value;
    }

    /**
     * Return whether the receiver should allow null values.
     */
    public boolean shouldAllowNull() {
        return shouldAllowNull;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getName() + "(" + getType() + "))";
    }

    /**
     *  <b>Purpose</b>: Define a database-platform-specific definition for a platform independent Java class type.
     *  This is used for the field creation within a table creation statement.
     *  <p><b>Responsibilities</b>:
     *  <ul>
     *    <li> Store a default size and know if the size option is required or optional.</li>
     *    <li> Store the name of the real database type.</li>
     *    <li> Maintain maximum precision and optional min &amp; max Scale.</li>
     *  </ul>
     */
    public static final class DatabaseType {
        //TODO: switch to 'record'?
        private final String name;
        private final String suffix;
        private final boolean allowNull;
        private final boolean allowSize;
        private final boolean requireSize;
        private final int defaultSize;
        private final int defaultSubSize;
        private final int maxPrecision;
        private final int minScale;
        private final int maxScale;

        public DatabaseType(String name) {
            this(name, "", true, true, false,
                    10, 0, 10, 0, 0);
        }

        public DatabaseType(String name, int defaultSize) {
            this(name, "", true, true, true,
                    defaultSize, 0, defaultSize, 0, 0);
        }

        public DatabaseType(String name, int defaultSize, String suffix) {
            this(name, suffix, true, true, true,
                    defaultSize, 0, defaultSize, 0, 0);
        }

        public DatabaseType(String name, int defaultSize, int defaultSubSize) {
            this(name, "", true, true, true,
                    defaultSize, defaultSubSize, defaultSize, 0, defaultSubSize);
        }

        public DatabaseType(String name, int defaultSize, int defaultSubSize,
                            int maxPrecision, int minScale, int maxScale) {
            this(name, "", true, true, true,
                    defaultSize, defaultSubSize, maxPrecision, minScale, maxScale);
        }

        public DatabaseType(String name, boolean allowSize) {
            this(name, "", true, allowSize, false,
                    10, 0, 10, 0, 0);
        }

        public DatabaseType(String name, boolean allowSize, boolean allowNull) {
            this(name, "", allowNull, allowSize, false,
                    10, 0, 10, 0, 0);
        }

//        public DatabaseType(String name, boolean allowNull, boolean allowSize, boolean requireSize,
//                            int defaultSize, int defaultSubSize, int maxPrecision, int minScale, int maxScale) {
//            this(name, "", allowNull, allowSize, false,
//                    10, 0, 10, 0, 0);
//        }

        public DatabaseType(String name, String suffix, boolean allowNull, boolean allowSize, boolean requireSize,
                            int defaultSize, int defaultSubSize, int maxPrecision, int minScale, int maxScale) {
            this.name = name;
            this.suffix = suffix;
            this.allowNull = allowNull;
            this.allowSize = allowSize;
            this.requireSize = requireSize;
            this.defaultSize = defaultSize;
            this.defaultSubSize = defaultSubSize;
            this.maxPrecision = maxPrecision;
            this.minScale = minScale;
            this.maxScale = maxScale;
        }

        public DatabaseType ofSize(int size) {
            return new DatabaseType(name, suffix, allowNull,
                    true, true, size, defaultSubSize,
                    size, minScale, maxScale);
        }

        public DatabaseType ofNoSize() {
            return new DatabaseType(name, suffix, allowNull,
                    false, false, 0, defaultSubSize,
                    0, minScale, maxScale);
        }

        /**
         * Return the name. Can be any database primitive type name,
         * this name will then be mapped to the Java primitive type,
         * the database type varies by platform, and the mappings can be found
         * in the subclasses of {@linkplain DDLPlatform}.
         * <p>
         * <table>
         *     <caption>Java names and their ODBC mappings include</caption>
         *     <tr><th>Java name</th><th>ODBC mapping</th></tr>
         *     <tr><td>Integer</td><td>SQL_INT</td></tr>
         *     <tr><td>Float</td><td>SQL_FLOAT</td></tr>
         *     <tr><td>Double</td><td>SQL_DOUBLE</td></tr>
         *     <tr><td>Long</td><td>SQL_LONG</td></tr>
         *     <tr><td>Short</td><td>SQL_INT</td></tr>
         *     <tr><td>BigDecimal</td><td>SQL_NUMERIC</td></tr>
         *     <tr><td>BigInteger</td><td>SQL_NUMERIC</td></tr>
         *     <tr><td>String</td><td>SQL_VARCHAR</td></tr>
         *     <tr><td>Array</td><td>BLOB</td></tr>
         *     <tr><td>Character[]</td><td>SQL_CHAR</td></tr>
         *     <tr><td>Boolean</td><td>SQL_BOOL</td></tr>
         *     <tr><td>Text</td><td>CLOB</td></tr>
         *     <tr><td>Date</td><td>SQL_DATE</td></tr>
         *     <tr><td>Time</td><td>SQL_TIME</td></tr>
         *     <tr><td>Timestamp</td><td>SQL_TIMESTAMP</td></tr>
         * </table>
         */
        public String name() {
            return name;
        }

        /**
         * Return if this type is allowed to be null for this platform
         */
        public boolean allowNull() {
            return allowNull;
        }

        /**
         * Return if this type can support a size specification.
         */
        public boolean allowSize() {
            return allowSize;
        }

        /**
         * Return if this type must have a size specification.
         */
        public boolean requireSize() {
            return requireSize;
        }

        /**
         * Return the default size for this type.
         * This default size will be used if the database requires specification of a size,
         * and the table definition did not provide one.
         */
        public int defaultSize() {
            return defaultSize;
        }

        /**
         * Return the default sub-size for this type.
         * This default size will be used if the database requires specification of a size,
         * and the table definition did not provide one.
         */
        public int defaultSubSize() {
            return defaultSubSize;
        }

        public int maxPrecision() {
            return maxPrecision;
        }

        public int maxScale() {
            return maxScale;
        }

        public int minScale() {
            return minScale;
        }

        public String suffix() {
            return suffix;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DatabaseType) obj;
            return Objects.equals(this.name, that.name) &&
                    this.allowNull == that.allowNull &&
                    this.allowSize == that.allowSize &&
                    this.requireSize == that.requireSize &&
                    this.defaultSize == that.defaultSize &&
                    this.defaultSubSize == that.defaultSubSize &&
                    this.maxPrecision == that.maxPrecision &&
                    this.minScale == that.minScale &&
                    this.maxScale == that.maxScale;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, allowNull, allowSize, requireSize, defaultSize, defaultSubSize, maxPrecision, minScale, maxScale);
        }

        @Override
        public String toString() {
            return "DatabaseType[" +
                    "name=" + name + ", " +
                    "allowNull=" + allowNull + ", " +
                    "allowSize=" + allowSize + ", " +
                    "requireSize=" + requireSize + ", " +
                    "defaultSize=" + defaultSize + ", " +
                    "defaultSubSize=" + defaultSubSize + ", " +
                    "maxPrecision=" + maxPrecision + ", " +
                    "maxScale=" + maxScale + ", " +
                    "minScale=" + minScale + ']';
        }
    }
}

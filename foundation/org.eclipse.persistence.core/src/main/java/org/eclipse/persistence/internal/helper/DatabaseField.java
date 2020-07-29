/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     tware - added handling of database delimiters
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     05/30/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
//     02/11/2013-2.5 Guy Pelletier
//       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
package org.eclipse.persistence.internal.helper;

//javase imports
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import static java.lang.Integer.MIN_VALUE;

//EclipseLink imports
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:
 * Define a fully qualified field name.<p>
 * <b>Responsibilities</b>:    <ul>
 * <li> Know its name and its table.
 * </ul>
 * @see DatabaseTable
 */
public class DatabaseField implements Cloneable, Serializable, CoreField {

    /** Variables used for generating DDL **/
    protected int scale;
    protected int length;
    protected int precision;
    protected boolean isUnique;
    protected boolean isNullable;
    protected boolean isUpdatable;
    protected boolean isInsertable;
    protected boolean isCreatable;
    protected boolean isPrimaryKey;
    protected String columnDefinition;

    /** Column name of the field. */
    protected String name;

    /** Fields table (encapsulates name + creator). */
    protected DatabaseTable table;

    /** PERF: Cache fully qualified table.field-name. */
    protected String qualifiedName;

    /**
     * Respective Java type desired for the field's value, used to optimize performance and for binding.
     * PERF: Allow direct variable access from getObject.
     */
    public transient Class type;
    public String typeName; // shadow variable - string name of above Class type variable

    /**
     * Respective JDBC type of the field's value.
     * This overrides the class type, which the JDBC type is normally computed from.
     * PERF: Allow direct variable access from getObject.
     */
    public int sqlType;

    /**
     * Store normal index of field in result set to optimize performance.
     * PERF: Allow direct variable access from getIndicatingNoEntry.
     */
    public int index;

    protected boolean useDelimiters = false;

    /**
     * used to represent the value when it has not being defined
     */
    public static final int NULL_SQL_TYPE = MIN_VALUE;

    /**
     * Returns true if this field was translated.
     */
    protected boolean isTranslated = false;

    /**
     * Indicates whether the field should be kept in the record after the object is created.
     * Used by ObjectLevelReadQuery ResultSetAccessOptimization.
     */
    public boolean keepInRow;

    public DatabaseField() {
        this("", new DatabaseTable());
    }

    public DatabaseField(String qualifiedName) {
        this(qualifiedName, null, null);
    }

    public DatabaseField(String qualifiedName, String startDelimiter, String endDelimiter) {
        this.index = -1;
        this.sqlType = NULL_SQL_TYPE;
        int index = qualifiedName.lastIndexOf('.');

        if (index == -1) {
            setName(qualifiedName, startDelimiter, endDelimiter);
            this.table = new DatabaseTable();
        } else {
            setName(qualifiedName.substring(index + 1, qualifiedName.length()), startDelimiter, endDelimiter);
            this.table = new DatabaseTable(qualifiedName.substring(0, index), startDelimiter, endDelimiter);
        }
        initDDLFields();
    }

    public DatabaseField(String fieldName, String tableName) {
        this(fieldName, new DatabaseTable(tableName));
    }

    public DatabaseField(String fieldName, DatabaseTable databaseTable) {
        this(fieldName, databaseTable, null, null);
    }

    public DatabaseField(String fieldName, DatabaseTable databaseTable, String startDelimiter, String endDelimiter) {
        this.index = -1;
        this.sqlType = NULL_SQL_TYPE;
        setName(fieldName, startDelimiter, endDelimiter);
        this.table = databaseTable;
        initDDLFields();
    }

    /**
     * Inits the DDL generation fields with our defaults. Note: we used to
     * initialize the length to the JPA default of 255 but since this default
     * value should only apply for string fields we set it to 0 to indicate
     * that it was not specified and rely on the default (255) to come from
     * individual platforms.
     */
    public void initDDLFields() {
        scale = 0;
        length = 0;
        precision = 0;
        isUnique = false;
        isNullable = true;
        isUpdatable = true;
        isInsertable = true;
        isCreatable = true;
        isPrimaryKey = false;
        columnDefinition = "";
    }

    /**
     * The table is not cloned because it is treated as an automatic value.
     */
    @Override
    public DatabaseField clone() {
        try {
            return (DatabaseField)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.getMessage());
        }
    }

    /*
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual
     * class-based settings. This method is implemented by subclasses as
     * necessary.
     * @param classLoader
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        if (type == null && typeName != null) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        type = AccessController.doPrivileged(new PrivilegedClassForName(typeName, true, classLoader));
                    } catch (PrivilegedActionException e) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(typeName, e.getException());
                    }
                } else {
                    type = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(typeName, true, classLoader);
                }
            } catch (Exception exception) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(typeName, exception);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // Defaulting to use UPPER case for the ignore case comparison
        // If all instances use UPPER case, then case doesn't matter
        String q = getQualifiedName().toUpperCase();
        if (DatabasePlatform.shouldIgnoreCaseOnFieldComparisons() && q != null) {
            result = prime * result + q.hashCode();
        } else {
            result = prime * result + ((q == null) ? 0 : q.hashCode());
        }
        return result;
    }


    /**
     * Determine whether the receiver is equal to a DatabaseField.
     * Return true if the receiver and field have the same name and table.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DatabaseField)) {
            return false;
        }

        return equals((DatabaseField)object);
    }

    /**
     * Determine whether the receiver is equal to a DatabaseField.
     * Return true if the receiver and field have the same name and table.
     */
    public boolean equals(DatabaseField field) {

        if(field == null) {
            return false;
        }

        if (this == field) {
            return true;
        }

        if (DatabasePlatform.shouldIgnoreCaseOnFieldComparisons()) {
            return getQualifiedName().equalsIgnoreCase(field.getQualifiedName());
        }
        return getQualifiedName().equals(field.getQualifiedName());
    }

    /**
     * Get the SQL fragment that is used when generating the DDL for the column.
     */
    public String getColumnDefinition() {
        return this.columnDefinition;
    }

    /**
     * Return the expected index that this field will occur in the result set
     * row. This is used to optimize performance of database row field lookups.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Used to specify the column length when generating DDL.
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Return the unqualified name of the field.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns this fields name with database delimiters if useDelimiters is true.
     * This method should be called any time the field name is requested for writing SQL.
     */
    public String getNameDelimited(DatasourcePlatform platform) {
        if (this.useDelimiters){
            return platform.getStartDelimiter() + this.name + platform.getEndDelimiter();
        }
        return this.name;
    }

    /**
     * Returns the precision for a decimal column when generating DDL.
     */
    public int getPrecision() {
        return this.precision;
    }

    /**
     * Returns the qualified DatabaseField name which is a concatenation of: this.table + '.' + this.fieldName
     * @return
     */
    public String getQualifiedName(){
        if (this.qualifiedName == null) {
            if (hasTableName()) {
                this.qualifiedName = this.table.getQualifiedName() + "." + getName();
            } else {
                this.qualifiedName = getName();
            }
        }
        return this.qualifiedName;
    }

    /**
     * Return the qualified name of the field.
     * PERF: Cache the qualified name.
     */
    public String getQualifiedNameDelimited(DatasourcePlatform platform) {
        if (hasTableName()) {
            return this.table.getQualifiedNameDelimited(platform) + "." + getNameDelimited(platform);
        } else {
            return getNameDelimited(platform);
        }
    }
    /**
     * Returns the scale for a decimal column when generating DDL.
     */
    public int getScale() {
        return this.scale;
    }

    public DatabaseTable getTable() {
        return table;
    }

    public String getTableName() {
        return getTable().getName();
    }

    public void setTableName(String tableName) {
        setTable(new DatabaseTable(tableName));
    }

    @Override
    public Class getType() {
        if ((this.type == null) && (this.typeName != null)) {
            convertClassNamesToClasses(getClass().getClassLoader());
        }
        return this.type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Return the JDBC type that corresponds to the field.
     * The JDBC type is normally determined from the class type,
     * but this allows it to be overridden for types that do not match directly to a Java type,
     * such as MONEY or ARRAY, STRUCT, XMLTYPE, etc.
     * This can be used for binding or stored procedure usage.
     */
    public int getSqlType() {
        return sqlType;
    }

    public boolean hasTableName() {
        if (this.table == null) {
            return false;
        }
        if (this.table.getName() == null) {
            return false;
        }
        return !(this.table.getName().equals(""));
    }

    /**
     *  PUBLIC:
     *  Return if this is an ObjectRelationalDatabaseField.
     */
    public boolean isObjectRelationalDatabaseField(){
        return false;
    }

    /**
     * Used to specify whether the column should be included in SQL UPDATE
     * statements.
     */
    public boolean isInsertable() {
        return this.isInsertable;
    }

    /**
     * Used for generating DDL. Returns true if the database column is
     * nullable.
     */
    public boolean isNullable() {
        return this.isNullable;
    }

    /**
     * Used to specify whether the column should be included in the primary
     * on the database table.
     */
    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    /**
     * Return true if this database field is a translation.
     */
    public boolean isTranslated() {
        return this.isTranslated;
    }

    /**
     * Used for generating DDL. Returns true if the field is a unique key.
     */
    public boolean isUnique() {
        return this.isUnique;
    }

    /**
     * Returns true is this database field should be read only.
     */
    public boolean isReadOnly() {
        return (! isUpdatable && ! isInsertable);
    }

    public boolean keepInRow() {
        return keepInRow;
    }

    /**
     * Returns whether the column should be included in SQL INSERT
     * statements.
     */
    public boolean isUpdatable() {
        return this.isUpdatable;
    }

    /**
     * Reset the field's name and table from the qualified name.
     */
    public void resetQualifiedName(String qualifiedName) {
        setIndex(-1);
        int index = qualifiedName.lastIndexOf('.');

        if (index == -1) {
            setName(qualifiedName);
            getTable().setName("");
            getTable().setTableQualifier("");
        } else {
            setName(qualifiedName.substring(index + 1, qualifiedName.length()));
            getTable().setPossiblyQualifiedName(qualifiedName.substring(0, index));
        }
    }

    /**
     * Set the SQL fragment that is used when generating the DDL for the column.
     */
    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    /**
     * Set the expected index that this field will occur in the result set row.
     * This is used to optimize performance of database row field lookups.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Used to specify whether the column should be included in SQL UPDATE
     * statements.
     */
    public void setInsertable(boolean isInsertable) {
        this.isInsertable = isInsertable;
    }

    public void setKeepInRow(boolean keepInRow) {
        this.keepInRow = keepInRow;
    }

    /**
     * Set the isTranslated flag.
     */
    public void setIsTranslated(boolean isTranslated) {
        this.isTranslated = isTranslated;
    }

    /**
     * Used to specify the column length when generating DDL.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Set the unqualified name of the field.
     */
    @Override
    public void setName(String name) {
        setName(name, null, null);
    }

    /**
     * Set the unqualified name of the field.
     *
     * If the name contains database delimiters, they will be stripped and a flag will be set to have them
     * added when the DatabaseField is written to SQL
     */
    public void setName(String name, DatasourcePlatform platform){
        setName(name, platform.getStartDelimiter(), platform.getEndDelimiter());
    }

    /**
     * Set the unqualified name of the field.
     *
     * If the name contains database delimiters, they will be stripped and a flag will be set to have them
     * added when the DatabaseField is written to SQL
     */
    public void setName(String name, String startDelimiter, String endDelimiter) {
        if ((startDelimiter != null) && (endDelimiter != null) && !startDelimiter.equals("") && !endDelimiter.equals("") && name.startsWith(startDelimiter) && name.endsWith(endDelimiter)){
            this.name = name.substring(startDelimiter.length(), name.length() - endDelimiter.length());
            this.useDelimiters = true;
        } else {
            this.name = name;
        }
        this.qualifiedName = null;
    }

    /**
     * Used for generating DDL. Set to true if the database column is
     * nullable.
     */
    public void setNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * Used to specify the precision for a decimal column when generating DDL.
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * Used to specify whether the column should be included in primary key
     * on the database table.
     */
    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    /**
     * Used to specify the scale for a decimal column when generating DDL.
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Set the JDBC type that corresponds to the field.
     * The JDBC type is normally determined from the class type,
     * but this allows it to be overridden for types that do not match directly
     * to a Java type, such as MONEY or ARRAY, STRUCT, XMLTYPE, etc.
     * This can be used for binding or stored procedure usage.
     */
    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    /**
     * Set the table for the field.
     */
    public void setTable(DatabaseTable table) {
        this.table = table;
        this.qualifiedName = null;
    }

    /**
     * Set the Java class type that corresponds to the field.
     * The JDBC type is determined from the class type,
     * this is used to optimize performance, and for binding.
     */
    @Override
    public void setType(Class type) {
        this.type = type;
        if (this.type != null && typeName == null) {
            typeName = this.type.getName();
        }
    }

    /**
     * Used for generating DDL. Set to true if the field is a unique key.
     */
    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    /**
     * Used to specify whether the column should be included in SQL INSERT
     * statements.
     */
    public void setUpdatable(boolean isUpdatable) {
        this.isUpdatable = isUpdatable;
    }

    @Override
    public String toString() {
        return this.getQualifiedName();
    }

    public void setUseDelimiters(boolean useDelimiters) {
        this.useDelimiters = useDelimiters;
    }

    public boolean shouldUseDelimiters() {
        return this.useDelimiters;
    }

    public boolean isCreatable() {
        return isCreatable;
    }

    public void setCreatable(boolean isCreatable) {
        this.isCreatable = isCreatable;
    }
}

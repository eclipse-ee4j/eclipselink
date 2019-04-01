/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Serializable;

import org.eclipse.persistence.internal.helper.Helper;

/**
 * INTERNAL:
 *    <b>Purpose</b>: Define a database platform specific definition for a platform independent Java class type.
 *    This is used for the field creation within a table creation statement.
 *    <p><b>Responsibilities</b>:
 *    <ul>
 *    <li> Store a default size and know if the size option is required or optional.</li>
 *    <li> Store the name of the real database type.</li>
 *    <li> Maintain maximum precision and optionall min &amp; max Scale.</li>
 *    </ul>
 */
public class FieldTypeDefinition implements Serializable {

    protected String name;
    protected int defaultSize;
    protected int defaultSubSize;
    protected boolean isSizeAllowed;
    protected boolean isSizeRequired;
    protected int maxPrecision;
    protected int minScale;
    protected int maxScale;
    protected boolean shouldAllowNull; //allow for specific types/platforms to not allow null
    protected String typesuffix;

    public FieldTypeDefinition() {
        defaultSize = 10;
        isSizeRequired = false;
        isSizeAllowed = true;
        maxPrecision = 10;
        minScale = 0;
        maxScale = 0;
        shouldAllowNull = true;
        typesuffix = null;
    }

   /**
    * Return a new field type.
    * @see #setName(String)
    */
    public FieldTypeDefinition(String databaseTypeName) {
        this();
        name = databaseTypeName;
    }

    /**
     * Return a new field type with a required size defaulting to the defaultSize.
     */
    public FieldTypeDefinition(String databaseTypeName, int defaultSize) {
        this();
        this.name = databaseTypeName;
        this.defaultSize = defaultSize;
        this.isSizeRequired = true;
        setMaxPrecision(defaultSize);
    }

    /**
     * Return a new field type with a required size defaulting to the defaultSize.
     */
    public FieldTypeDefinition(String databaseTypeName, int defaultSize, int defaultSubSize) {
        this();
        this.name = databaseTypeName;
        this.defaultSize = defaultSize;
        this.defaultSubSize = defaultSubSize;
        this.isSizeRequired = true;
        setMaxPrecision(defaultSize);
        setMaxScale(defaultSubSize);
    }

    public FieldTypeDefinition(String databaseTypeName, int defaultSize, String aTypesuffix) {
        this(databaseTypeName, defaultSize);
        this.typesuffix = aTypesuffix;
        this.isSizeAllowed = true;
    }

    /**
     * Return a new field type with a required size defaulting to the defaultSize.
     */
    public FieldTypeDefinition(String databaseTypeName, boolean allowsSize) {
        this();
        this.name = databaseTypeName;
        this.isSizeAllowed = allowsSize;
    }

    /** Return a new field type with a required size defaulting to the defaultSize and
     *  shouldAllowNull set to allowsNull.
     */
    public FieldTypeDefinition(String databaseTypeName, boolean allowsSize, boolean allowsNull) {
        this(databaseTypeName, allowsSize);
        this.shouldAllowNull = allowsNull;
    }

    /**
     * Return the default size for this type.
     * This default size will be used if the database requires specification of a size,
     * and the table definition did not provide one.
     */
    public int getDefaultSize() {
        return defaultSize;
    }

    /**
    * Return the default sub-size for this type.
    * This default size will be used if the database requires specification of a size,
    * and the table definition did not provide one.
    */
    public int getDefaultSubSize() {
        return defaultSubSize;
    }

    public int getMaxPrecision() {
        return maxPrecision;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public int getMinScale() {
        return minScale;
    }

    /**
    * Return the name. Can be any database primitive type name,
    * this name will then be mapped to the Java primitive type,
    * the database type varies by platform and the mappings can be found in the subclasses of DatabasePlatform.
    *
    *    these Java names and their ODBC mappings include;
    *        - Integer        -&gt; SQL_INT
    *        - Float            -&gt; SQL_FLOAT
    *        - Double            -&gt; SQL_DOUBLE
    *        - Long            -&gt; SQL_LONG
    *        - Short            -&gt; SQL_INT
    *        - BigDecimal    -&gt; SQL_NUMERIC
    *        - BigInteger    -&gt; SQL_NUMERIC
    *        - String            -&gt; SQL_VARCHAR
    *        - Array            -&gt; BLOB
    *        - Character[]    -&gt; SQL_CHAR
    *        - Boolean        -&gt; SQL_BOOL
    *        - Text            -&gt; CLOB
    *        - Date            -&gt; SQL_DATE
    *        - Time            -&gt; SQL_TIME
    *        - Timestamp    -&gt; SQL_TIMESTAMP
    *
    * @see org.eclipse.persistence.internal.databaseaccess.DatabasePlatform
    */
    public String getName() {
        return name;
    }

    /**
     * Returns a type suffix (like unicode, byte or ascii) for maxdb create table stmts
     */
    public String getTypesuffix() {
        return typesuffix;
    }

    /**
    * Return if this type can support a size specification.
    */
    public boolean isSizeAllowed() {
        return isSizeAllowed;
    }

    /**
    * Return if this type must have a size specification.
    */
    public boolean isSizeRequired() {
        return isSizeRequired;
    }

    /**
     * Return if this type is allowed to be null for this platform
     */
    public boolean shouldAllowNull() {
        return this.shouldAllowNull;
    }

    /**
    * Set the default size for this type.
    * This default size will be used if the database requires specification of a size,
    * and the table definition did not provide one.
    */
    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    /**
    * Set the default sub-size for this type.
    * This default size will be used if the database requires specification of a size,
    * and the table definition did not provide one.
    */
    public void setDefaultSubSize(int defaultSubSize) {
        this.defaultSubSize = defaultSubSize;
    }

    /**
    * Set if this type can support a size specification.
    */
    public void setIsSizeAllowed(boolean aBoolean) {
        isSizeAllowed = aBoolean;
    }

    /**
    * Set if this type must have a size specification.
    */
    public void setIsSizeRequired(boolean aBoolean) {
        isSizeRequired = aBoolean;
    }

    /**
     * Set if this type is allowed to be null for this platform
     */
    public void setShouldAllowNull(boolean allowsNull) {
        this.shouldAllowNull = allowsNull;
    }

    /**
     *    Set the maximum precision and the minimum and maximum scale.
     *    @return    this    Allowing the method to be invoked inline with constructor
     */
    public FieldTypeDefinition setLimits(int maxPrecision, int minScale, int maxScale) {
        setMaxPrecision(maxPrecision);
        setMinScale(minScale);
        setMaxScale(maxScale);
        return this;
    }

    public void setMaxPrecision(int maximum) {
        maxPrecision = maximum;
    }

    public void setMaxScale(int maximum) {
        maxScale = maximum;
    }

    public void setMinScale(int minimum) {
        minScale = minimum;
    }

    /**
    * Set the name.
    * @param name can be any database primitive type name,
    * this name will then be mapped to the Java primitive type,
    * the database type varies by platform and the mappings can be found in the subclasses of DatabasePlatform.
    *
    *    these Java names and their ODBC mappings include;
    *        - Integer        -&gt; SQL_INT
    *        - Float            -&gt; SQL_FLOAT
    *        - Double        -&gt; SQL_DOUBLE
    *        - Long            -&gt; SQL_LONG
    *        - Short            -&gt; SQL_INT
    *        - BigDecimal    -&gt; SQL_NUMERIC
    *        - BigInteger    -&gt; SQL_NUMERIC
    *        - String        -&gt; SQL_VARCHAR
    *        - Array            -&gt; BLOB
    *        - Character[]    -&gt; SQL_CHAR
    *        - Boolean        -&gt; SQL_BOOL
    *        - Text            -&gt; CLOB
    *        - Date            -&gt; SQL_DATE
    *        - Time            -&gt; SQL_TIME
    *        - Timestamp    -&gt; SQL_TIMESTAMP
    *
    * @see org.eclipse.persistence.internal.databaseaccess.DatabasePlatform
    */
    public void setName(String name) {
        this.name = name;
    }

    /**
    * Set this type to not allow a size specification.
    */
    public void setSizeDisallowed() {
        setIsSizeAllowed(false);
    }

    /**
    * Set this type to optionally have a size specification.
    */
    public void setSizeOptional() {
        setIsSizeRequired(false);
        setIsSizeAllowed(true);
    }

    /**
    * Set this type to require to have a size specification.
    */
    public void setSizeRequired() {
        setIsSizeRequired(true);
    }

    @Override
    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getName() + ")";
    }
}

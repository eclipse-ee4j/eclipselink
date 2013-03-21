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
package org.eclipse.persistence.platform.database;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

/**
 *  TopLink Platform class which works with Attunity's Connect JDBC driver.
 */
public class AttunityPlatform extends org.eclipse.persistence.platform.database.DatabasePlatform {
    public AttunityPlatform() {
        // For TEXT and IMAGE fields, streams must be used for binding
        usesStreamsForBinding = true;
        // Autocomit did not work as expected when this class was written.
        supportsAutoCommit = false;
    }

    /**
     *  Create a table which can translate between java types and Attunity Connect
     *  data types.
     *  @return java.util.Hashtable
     */
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("TINYINT", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("NUMERIC", 10));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMERIC", 19));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("NUMERIC", 19, 4));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("NUMERIC", 19, 4));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("NUMERIC", 5));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("NUMERIC", 3));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMERIC", 38));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DOUBLE", false));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("IMAGE", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("TEXT", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("IMAGE", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("TEXT", false));        
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("IMAGE", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("TEXT", false));
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }

    /**
    * Initialize any platform-specific operators
    */
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Concat, "CONCAT"));
    }

    public boolean isAttunity() {
        return true;
    }

    /**
     *  Attunity Connect does not support specifying the primary key in the create table
     *  syntax.
     *  @return boolean false
     */
    public boolean supportsPrimaryKeyConstraint() {
        return false;
    }

    /**
     *  Attunity Connect does not support creating foreign key constraints with the ALTER TABLE
     *  syntax.
     *  @return boolean false.
     */
    public boolean supportsForeignKeyConstraints() {
        return false;
    }
}

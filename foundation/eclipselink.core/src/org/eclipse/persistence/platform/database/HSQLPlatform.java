/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

/**
 *    <p><b>Purpose</b>: Provides HSQL specific behavior.
 *
 * @since TOPLink/Java 4.5
 */
public class HSQLPlatform extends DatabasePlatform {
    public HSQLPlatform() {
    }

    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = super.buildFieldTypes();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("TINYINT", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMERIC", 19));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("REAL", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("REAL", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMERIC", 38));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(38, -19, 19));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(38, -19, 19));
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BINARY", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("LONGVARCHAR", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BINARY", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("LONGVARCHAR", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BINARY", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("LONGVARCHAR", false));        

        return fieldTypeMapping;
    }

    public boolean isHSQL() {
        return true;
    }

    /**
     * HSQL 1.6.1 does not support the ALTER TABLE method of create foreign key constraints
     */
    public boolean supportsForeignKeyConstraints() {
        return false;
    }
}
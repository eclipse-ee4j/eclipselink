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
 *
 * @author Kyle Chen
 *
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.util.Hashtable;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

/**
 *    <p><b>Purpose</b>: Provides TimesTen 7 specific behavior.
 *
 */

public class TimesTen7Platform extends TimesTenPlatform {
    
    /**
     * Return the mapping of class types to database types for the schema framework.
     */
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;
        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("TT_TINYINT", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("TT_INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("TT_BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("TT_SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("TT_TINYINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("TT_BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL(38)", false));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL(38)", false));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", 255));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("TT_VARBINARY", 64000));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("VARCHAR", 64000));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("TT_VARBINARY", 64000));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("VARCHAR", 64000));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("TT_VARBINARY", 64000));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("VARCHAR", 64000));        
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        return fieldTypeMapping;
    }
    
    public boolean isTimesTen7() {
        return true;
    }
}

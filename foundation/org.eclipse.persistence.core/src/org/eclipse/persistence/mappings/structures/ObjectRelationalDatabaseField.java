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
package org.eclipse.persistence.mappings.structures;

import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * INTERNAL:
 * A database field of object-relational type: either a java.sql.Array,
 * java.sql.Struct, or java.sql.Ref.
 * Oracle drivers require the user defined field type name for these fields,
 * along with the generic sqlType: ARRAY, STRUCT, or REF.
 * Toplink can only recognize primitive field types like Integer
 * or String, but here custom java objects are being written to a single field.
 * Thus instead of DatabaseField#type the driver needs a string representing
 * the user defined type of the structure on the database, and the type of
 * field: either ARRAY, STRUCT, or REF.
 * Added for bug 2730536.
 * @author Stephen McRitchie
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class ObjectRelationalDatabaseField extends DatabaseField {
    protected String sqlTypeName;
    protected DatabaseField nestedTypeField;

    public ObjectRelationalDatabaseField(DatabaseField field) {
        this.index = field.index;
        this.name = field.getName();
        this.table = field.getTable();
        this.type = field.type;
        this.useDelimiters = field.shouldUseDelimiters();
        this.useUpperCaseForComparisons = field.getUseUpperCaseForComparisons();
        this.nameForComparisons = field.getNameForComparisons();
        this.typeName = field.getTypeName();
        this.sqlTypeName = "";
    }

    public ObjectRelationalDatabaseField(String name) {
        super(name);
        this.sqlTypeName = "";
    }

    /*
     * INTERNAL:
     * Convert all the class-name-based settings in this mapping to actual 
     * class-based settings. This method is implemented by subclasses as 
     * necessary.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        super.convertClassNamesToClasses(classLoader);
        
        if (nestedTypeField != null) {
            nestedTypeField.convertClassNamesToClasses(classLoader);
        }
    }

    /**
     * ADVANCED:
     * For ARRAY and STRUCT fields, this is the user defined type for the field.
     * For REF fields, this is the user defined type of entity is points to.
     */
    public String getSqlTypeName() {
        return sqlTypeName;
    }
    
    /**
     *  PUBLIC:
     *  Return if this is an ObjectRelationalDatabaseField.
     */
    public boolean isObjectRelationalDatabaseField(){
        return true;
    }

    /**
     * ADVANCED:
     * For ARRAY and STRUCT fields, this is the user defined type for the field.
     * For REF fields, this is the user defined type of entity is points to.
     */
    public void setSqlTypeName(String sqlTypeName) {
        this.sqlTypeName = sqlTypeName;
    }
    
    /**
     * ADVANCED:
     * For ARRAY fields, this field's type represents the type contained in the ARRAY.
     */
    public DatabaseField getNestedTypeField() {
        return nestedTypeField;
    }

    /**
     * ADVANCED:
     * For ARRAY fields, this field's type represents the type contained in the ARRAY.
     */
    public void setNestedTypeField(DatabaseField nestedTypeField) {
        this.nestedTypeField = nestedTypeField;
    }
}

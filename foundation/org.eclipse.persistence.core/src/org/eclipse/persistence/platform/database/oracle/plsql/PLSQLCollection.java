/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - Dec 2008 proof-of-concept
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.plsql;

//javase imports
import java.sql.Array;
import java.util.Vector;
import static java.sql.Types.ARRAY;

//EclipseLink imports
import org.eclipse.persistence.internal.dynamicpersist.BaseEntity;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.queries.StoredProcedureCall;

@SuppressWarnings("unchecked")
public class PLSQLCollection extends ComplexDatabaseType implements Cloneable, OraclePLSQLType {

    /**
     * Defines the database type of the value contained in the collection type.
     * <p>i.e. the OF type.
     * <p>This could be a JDBC type, PLSQL type, or a PLSQL RECORD type.
     */
    protected DatabaseType nestedType;
    
    public PLSQLCollection() {
        super();
        this.javaType = Vector.class;
    }

    @Override
    public PLSQLCollection clone()  {
        PLSQLCollection clone = (PLSQLCollection)super.clone();
        return clone;
    }
    
    public boolean isCollection() {
        return true;
    }
    
    /**
     * Return the database type of the value contained in the collection type.
     */
    public DatabaseType getNestedType() {
        return nestedType;
    }
    /**
     * Set the database type of the value contained in the collection type.
     * <p>i.e. the OF type.
     * <p>This could be a JDBC type, PLSQL type, or a PLSQL RECORD type.
     */
    public void setNestedType(DatabaseType nestedType) {
        this.nestedType = nestedType;
    }
    public int getSqlCode() {
        return ARRAY;
    }

    @Override
    public void translate(PLSQLargument arg, AbstractRecord translationRow,
        AbstractRecord copyOfTranslationRow, Vector copyOfTranslationFields,
        Vector translationRowFields, Vector translationRowValues, StoredProcedureCall call) {
        Object f = copyOfTranslationFields.get(arg.originalIndex);
        if (f instanceof DatabaseField) {
            DatabaseField df = (DatabaseField)f;
            if (df.getName().equals(arg.name)) {
                Object value = copyOfTranslationRow.get(df.getName());
                if (value instanceof BaseEntity) {
                    // dynamic persistence
                    BaseEntity be = (BaseEntity)value;
                    AbstractSession s = call.getQuery().getSession();
                    Array convertedValue = (Array)((ObjectRelationalDataTypeDescriptor)s.
                        getDescriptorForAlias(arg.name)).buildFieldValueFromDirectValues((Vector)be.get(0),
                          ((ComplexDatabaseType)arg.databaseTypeWrapper.getWrappedType()).getCompatibleType(),s);
                    copyOfTranslationRow.put(df.getName(), convertedValue);
                }
            }
        }
        super.translate(arg, translationRow, copyOfTranslationRow, copyOfTranslationFields,
            translationRowFields, translationRowValues, call);
    }
}
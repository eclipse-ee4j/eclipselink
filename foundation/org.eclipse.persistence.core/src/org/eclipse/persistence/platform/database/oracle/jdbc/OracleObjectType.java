package org.eclipse.persistence.platform.database.oracle.jdbc;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;

public class OracleObjectType extends ComplexDatabaseType implements Cloneable {

    public OracleObjectType() {
        super();
    }

    protected int lastFieldIdx;
    protected Map<String, DatabaseType> fields =  new LinkedHashMap<String, DatabaseType>();
    
    public int getLastFieldIndex() {
        return lastFieldIdx;
    }
    public void setLastFieldIndex(int lastFieldIdx) {
        this.lastFieldIdx = lastFieldIdx;
    }

    public Map<String, DatabaseType> getFields() {
        return fields;
    }

    @Override
    public boolean isJDBCType() {
        return true;
    }
    
    @Override
    public boolean isComplexDatabaseType() {
        return true;
    }
    
    @Override
    public int getSqlCode() {
        return Types.JAVA_OBJECT;
    }

}
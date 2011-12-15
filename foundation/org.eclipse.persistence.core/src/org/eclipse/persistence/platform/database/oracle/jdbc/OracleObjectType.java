package org.eclipse.persistence.platform.database.oracle.jdbc;

import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;

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

    public void setFields(Map<String, DatabaseType> fields) {
        this.fields = fields;
    }
    @Override
    public boolean isJDBCType() {
        return true;
    }
    
    @Override
    public boolean isComplexDatabaseType() {
        return true;
    }

    public int getSqlCode() {
        return Types.JAVA_OBJECT;
    }
    public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
    	// no-op
    }

    public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
    	// Validate.
    	if (!hasCompatibleType()) {
    		throw QueryException.compatibleTypeNotSet(this);
    	}
    	if ((getTypeName() == null) || getTypeName().equals("")) {
    		throw QueryException.typeNameNotSet(this);
    	}
        sb.append("  ");
        sb.append(databaseTypeHelper.buildTarget(inArg));
        sb.append(" ");
        sb.append(getTypeName());
        sb.append(" := :");
        sb.append(inArg.inIndex);
        sb.append(";");
        sb.append(NL);
    }

    public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
        String target = databaseTypeHelper.buildTarget(outArg);
        sb.append("  :");
        sb.append(outArg.outIndex);
        sb.append(" := ");
        sb.append(target);
        sb.append(";");
        sb.append(NL);
    }
}
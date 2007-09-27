/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.platform.database.oracle;

// javase imports
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;

// TopLink imports
import org.eclipse.persistence.internal.databaseaccess.OutputParameterForCallableStatement;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.queries.StoredProcedureCall;

import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;

/**
 * <b>PUBLIC</b>: describe an Oracle PL/SQL Record type
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
public class OracleRecord implements ComplexDatabaseType, OraclePLSQLType {

    public static final String UNDERSCORE_TARGET = "_TARGET";
    public static final String UNDERSCORE_ORIG = "_ORIG";

    protected String name;
    protected String type;
    protected List<DatabaseField> fields;
    boolean hasCompatibleType = false;
    protected String compatibleTypeName;
    protected StoredProcedureCall storedProcedureCall;

    public OracleRecord() {
        super();
        fields = new ArrayList<DatabaseField>();
    }

    public boolean isComplexDatabaseType() {
        return true;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public List<DatabaseField> getFields() {
        return fields;
    }
    public void addField(DatabaseField field) {
        fields.add(field);
    }

    public boolean hasCompatibleType() {
        return hasCompatibleType;
    }
    public void setHasCompatibleType(boolean hasCompatibleType) {
        this.hasCompatibleType = hasCompatibleType;
    }

    public String getCompatibleTypeName() {
        return compatibleTypeName;
    }

    public void setCompatibleTypeName(String compatibleTypeName) {
        this.compatibleTypeName = compatibleTypeName;
        if (compatibleTypeName != null && compatibleTypeName.length() > 0) {
            hasCompatibleType = true;
        }
    }

    public int getTypeCode() {
        if (hasCompatibleType) {
            return STRUCT;
        }
        else {
            return OTHER;
        }
    }

    public String getTypeName() {
        return name;
    }

    public StoredProcedureCall getCall() {
        return storedProcedureCall;
    }
    public void setCall(StoredProcedureCall storedProcedureCall) {
        this.storedProcedureCall = storedProcedureCall;
    }

    public String buildTargetDeclaration(DatabaseField databaseField, Integer direction, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        if (direction == IN) {
            if (hasCompatibleType) {
                sb.append(getName());
                sb.append(UNDERSCORE_ORIG);
                sb.append(" ");
                sb.append(getCompatibleTypeName());
                sb.append(" := :");
                sb.append(index);
                sb.append(";\n ");
            }
            else {
                for (int i = 0, len = fields.size(); i < len; i++) {
                    DatabaseField f = fields.get(i);
                    sb.append(f.getDatabaseType().buildTargetDeclaration(f, direction, index + i));
                    sb.append("\n ");
                }
            }
        }
        else if (direction == OUT &&
			(getCompatibleTypeName() != null) && (getCompatibleTypeName().length() > 0)) {
            sb.append(getName());
            sb.append(UNDERSCORE_ORIG);
            sb.append(" ");
            sb.append(getCompatibleTypeName());
            sb.append(" := ");
            sb.append(getCompatibleTypeName());
            sb.append("(");
            for (int i = 0, len = fields.size(); i < len; i++) {
                sb.append("null");
                if (i < len-1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            sb.append(";\n ");
        }
        sb.append(getName());
        sb.append(UNDERSCORE_TARGET);
        sb.append(" ");
        sb.append(getType());
        sb.append(";\n");
        return sb.toString();
    }

    public String buildBeginBlock(DatabaseField databaseField, Integer direction, int index) {

        if (direction == IN) {
            StringBuilder sb = new StringBuilder();
            String target = getName() + UNDERSCORE_TARGET;
            String orig = getName() + UNDERSCORE_ORIG;
            for (int i = 0, len = fields.size(); i < len; i++) {
                DatabaseField f = fields.get(i);
                sb.append(" ");
                sb.append(target);
                sb.append('.');
                sb.append(f.getName());
                sb.append(" := ");
                if (hasCompatibleType) {
                    sb.append(orig);
                    sb.append('.');
                }
                sb.append(f.getName());
                if (!hasCompatibleType) {
                    sb.append(UNDERSCORE_TARGET);
                }
                sb.append(";\n");
            }
            return sb.toString();
        }
        else {
            return null;
        }

    }

    public Object getValueForInParameter(Object parameter, Vector parametersValues,
        AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session,
        boolean shouldBind) {

        int lengthMinusOne = fields.size() - 1;
        if (lengthMinusOne < 0) {
            return null;
        }
        if (hasCompatibleType) {
            return modifyRow.getValues(getName());
        }
        else {
            if (parametersValues != null) {
                for (int i = 0; i < lengthMinusOne; i++) {
                    parametersValues.add(modifyRow.getValues(fields.get(i)));
                }
                return modifyRow.getValues(fields.get(lengthMinusOne));
            }
            else {
                return null; // TODO - figure out what to do in this case
            }
        }
    }

    public String buildOutAssignment(DatabaseField databaseField, Integer direction, int index) {

        if (direction == OUT) {
            StringBuilder sb = new StringBuilder();
            String target = getName() + UNDERSCORE_TARGET;
            String orig = getName() + UNDERSCORE_ORIG;
            for (int i = 0, len = fields.size(); i < len; i++) {
                DatabaseField f = fields.get(i);
                if (hasCompatibleType) {
                    sb.append(orig);
                    sb.append('.');
                    sb.append(f.getName());
                }
                else {
                    sb.append(" :");
                    sb.append(index + i);
                }
                sb.append(" := ");
                sb.append(target);
                sb.append('.');
                sb.append(f.getName());
                sb.append(";\n ");
            }
            if (hasCompatibleType) {
                sb.append(":");
                sb.append(index);
                sb.append(" := ");
                sb.append(orig);
                sb.append(";\n ");
            }
            return sb.toString();
        }
        else {
            return null;
        }
    }

    public void setComplexOutParameterValue(PreparedStatement statement, int index) {

        if (hasCompatibleType) {
            try {
                ((CallableStatement)statement).registerOutParameter(index, STRUCT,
                    getCompatibleTypeName());
            }
            catch (SQLException e) {
                // not sure what to do
            }
        }
        else {
            for (int i = 0, len = fields.size(); i < len; i++) {
                DatabaseField f = fields.get(i);
                try {
                    if (f.getDatabaseType() == JDBCTypes.NUMERIC_TYPE) {
                        ((CallableStatement)statement).registerOutParameter(index+i,
                            f.getSqlType(), f.getScale());
                    }
                    else {
                        ((CallableStatement)statement).registerOutParameter(index+i,
                            f.getSqlType());
                    }
                }
                catch (SQLException sqlException) {
                    // not sure what to do
                }
            }
        }
    }

    public void buildOutputRow(AbstractRecord row, CallableStatement statement, int index) {

        if (hasCompatibleType) {
            Object value = null;
            try {
                value = statement.getObject(index);
                AbstractRecord nestedRow = ((ObjectRelationalDataTypeDescriptor)storedProcedureCall.
                    getQuery().getDescriptor()).buildRowFromStructure((Struct)value);
                row.mergeFrom(nestedRow);
            }
            catch (SQLException sqlException) {
                // ignore - value not available at that index
            }
        }
        else {
            for (int i = 0, len = fields.size(); i < len; i++) {
                DatabaseField f = fields.get(i);
                Object value = null;
                try {
                    value = statement.getObject(index + i);
                }
                catch (SQLException sqlException) {
                    // ignore - value not available at that index
                }
                row.put(f, value);
            }
        }
    }

    public void setConversionType(DatabaseField databaseField) {
    }

}

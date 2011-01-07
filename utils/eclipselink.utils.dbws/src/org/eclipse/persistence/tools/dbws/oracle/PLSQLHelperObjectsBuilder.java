/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

//EclipseLink
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleArrayType;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleObjectType;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherDefaultListener;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

public class PLSQLHelperObjectsBuilder extends PublisherDefaultListener {

    protected Map<String, DatabaseType[]> methodTypeMap = new HashMap<String, DatabaseType[]>();
    protected Map<String, DatabaseType> knownDatabaseTypesMap = new HashMap<String, DatabaseType>();
    protected Stack<DatabaseType> typeStack = new Stack<DatabaseType>();
    protected String packageName = null;
    protected String schemaName = null;
    protected String currentMethodName = null;
    protected int currentMethodArgIdx = -1;
    protected DBWSBuilder dbwsBuilder;

    public PLSQLHelperObjectsBuilder(DBWSBuilder dbwsBuilder) {
        this.dbwsBuilder = dbwsBuilder;
    }

    public String trimOffSchemaName(String s) {
        if (schemaName != null) {
            if (s.startsWith(schemaName)) {
                return s.substring(schemaName.length() + 1);
            }
        }
        return s;
    }

    public DatabaseType[] getTypesForMethod(String methodName) {
        return methodTypeMap.get(methodName);
    }

    public Map<String, DatabaseType[]> getMethodTypeMap() {
        return methodTypeMap;
    }

    public DatabaseType getKnownDatabaseType(String typeName) {
        return knownDatabaseTypesMap.get(typeName);
    }

    public void putKnownDatabaseType(String typeName, DatabaseType databaseType) {
        knownDatabaseTypesMap.put(typeName, databaseType);
    }

    @Override
    public void beginPackage(String packageName) {
        // trim-off schema name
        int dotIdx = packageName.indexOf('.');
        if (dotIdx > -1) {
            schemaName = packageName.substring(0, dotIdx);
            this.packageName = packageName.substring(dotIdx + 1);
        }
    }

    @Override
    public void beginPlsqlTable(String tableName, String targetTypeName) {
        PLSQLCollection plsqlCollection = null;
        boolean found = false;
        for (DatabaseType[] databaseTypes : methodTypeMap.values()) {
            if (found) {
                break;
            }
            for (int i = 0, len = databaseTypes.length; i < len; i++) {
                DatabaseType databaseType = databaseTypes[i];
                if (databaseType != null && databaseType.isComplexDatabaseType()) {
                    ComplexDatabaseType cdt = (ComplexDatabaseType)databaseType;
                    if (cdt.isCollection()) {
                        PLSQLCollection tmp = (PLSQLCollection)cdt;
                        if (tmp.getCompatibleType().equalsIgnoreCase(targetTypeName)) {
                            found = true;
                            plsqlCollection = tmp;
                            break;
                        }
                    }
                }
            }
        }
        if (plsqlCollection == null) {
            plsqlCollection = new PLSQLCollection();
            plsqlCollection.setTypeName(trimOffSchemaName(tableName));
            plsqlCollection.setCompatibleType(targetTypeName);
            putKnownDatabaseType(tableName, plsqlCollection);
        }
        typeStack.push(plsqlCollection);
    }

    @Override
    public void endPlsqlTable(String tableName, String typeDeclaration, String typeDropDDL) {
        if (!"".equals(typeDeclaration)) {
            dbwsBuilder.getTypeDDL().add(typeDeclaration);
            dbwsBuilder.getTypeDropDDL().add(typeDropDDL);
        }
        PLSQLCollection plsqlCollection = (PLSQLCollection)typeStack.pop();
        plsqlCollection.setJavaTypeName(
            plsqlCollection.getTypeName().toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
        if (!typeStack.empty()) {
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                if (cdt.isCollection()) {
                    ((PLSQLCollection)cdt).setNestedType(plsqlCollection);
                }
                else if (cdt.isRecord()) {
                    List<PLSQLargument> fields = ((PLSQLrecord)cdt).getFields();
                    PLSQLargument field = fields.get(fields.size() - 1);
                    if (field.databaseType == null) {
                        field.databaseType = plsqlCollection;
                    }
                }
            }
        }
        else {
            typeStack.push(plsqlCollection);
        }
    }

    @Override
    public void beginPlsqlRecord(String plsqlRecordName, String targetTypeName, int numFields) {
        PLSQLrecord plsqlRecord = null;
        boolean found = false;
        for (DatabaseType[] databaseTypes : methodTypeMap.values()) {
            if (found) {
                break;
            }
            for (int i = 0, len = databaseTypes.length; i < len; i++) {
                DatabaseType databaseType = databaseTypes[i];
                if (databaseType != null && databaseType.isComplexDatabaseType()) {
                    ComplexDatabaseType cdt = (ComplexDatabaseType)databaseType;
                    if (cdt.isRecord()) {
                        PLSQLrecord tmp = (PLSQLrecord)cdt;
                        if (tmp.getCompatibleType().equalsIgnoreCase(targetTypeName)) {
                            found = true;
                            plsqlRecord = tmp;
                            break;
                        }
                    }
                }
            }
        }
        if (plsqlRecord == null) {
            plsqlRecord = new PLSQLrecord();
            plsqlRecord.setTypeName(trimOffSchemaName(plsqlRecordName));
            plsqlRecord.setCompatibleType(targetTypeName);
            plsqlRecord.setJavaTypeName(plsqlRecord.getTypeName().toLowerCase());
            putKnownDatabaseType(plsqlRecordName, plsqlRecord);
        }
        typeStack.push(plsqlRecord);
    }

    @Override
    public void beginPlsqlRecordField(String fieldName, int idx) {
        PLSQLrecord plsqlRecord = (PLSQLrecord)typeStack.peek();
        boolean found = false;
        for (PLSQLargument arg : plsqlRecord.getFields()) {
            if (arg.name.equalsIgnoreCase(fieldName)) {
                found = true;
                break;
            }
        }
        if (!found) {
            plsqlRecord.addField(fieldName, null);
        }
    }

    @Override
    public void endPlsqlRecord(String plsqlRecordName, String typeDeclaration, String typeDropDDL) {
        if (!"".equals(typeDeclaration)) {
            dbwsBuilder.getTypeDDL().add(typeDeclaration);
            dbwsBuilder.getTypeDropDDL().add(typeDropDDL);
        }
        PLSQLrecord plsqlRecord = (PLSQLrecord)typeStack.pop();
        if (!typeStack.empty()) {
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                if (cdt.isCollection()) {
                    PLSQLCollection coll = (PLSQLCollection)cdt;
                    if (coll.getNestedType() == null) {
                        coll.setNestedType(plsqlRecord);
                    }
                }
                else if (cdt.isRecord()) {
                    PLSQLrecord rec = (PLSQLrecord)cdt;
                    List<PLSQLargument> fields = rec.getFields();
                    PLSQLargument arg = fields.get(fields.size() - 1);
                    if (arg.databaseType == null) {
                        arg.databaseType = plsqlRecord;
                    }
                }
            }
        }
        else {
            typeStack.push(plsqlRecord);
        }
    }

    @Override
    public void beginMethod(String methodName, int numArgs) {
        methodTypeMap.put(methodName, new DatabaseType[numArgs]);
        currentMethodName = methodName;
    }

    @Override
    public void beginMethodArg(String argName, String direction, int idx) {
        currentMethodArgIdx = idx;
    }

    @Override
    public void endMethodArg(String argName) {
        DatabaseType[] methodType = methodTypeMap.get(currentMethodName);
        methodType[currentMethodArgIdx] = typeStack.pop();
    }

    @Override
    public void endMethod(String methodName) {
        currentMethodName = null;
        currentMethodArgIdx = -1;
    }

    @Override
    public void handleSqlType(String sqlTypeName, int typecode, String targetType) {
        DatabaseType databaseType = JDBCTypes.getDatabaseTypeForCode(typecode);
        if (databaseType == null) {
            databaseType = OraclePLSQLTypes.getDatabaseTypeForCode(
                trimOffSchemaName(sqlTypeName));
            if (databaseType == null) {
                databaseType = getKnownDatabaseType(sqlTypeName);
            }
        }
        if (!typeStack.empty()) {
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                if (cdt.isCollection()) {
                    if (cdt.isJDBCType()) {
                        //TODO Oracle Varray
                    }
                    else {
                        PLSQLCollection coll = (PLSQLCollection)cdt;
                        if (coll.getNestedType() == null) {
                            coll.setNestedType(databaseType);
                        }
                    }
                }
                else {
                    if (cdt.isRecord()) {
                        PLSQLrecord rec = (PLSQLrecord)cdt;
                        List<PLSQLargument> fields = rec.getFields();
                        PLSQLargument arg = fields.get(fields.size() - 1);
                        if (arg.databaseType == null) {
                            arg.databaseType = databaseType;
                        }
                    }
                    else if (cdt.isJDBCType()) {
                        OracleObjectType oot = (OracleObjectType)cdt;
                        Map<String, DatabaseType> fields = oot.getFields();
                        Object[] keys = oot.getFields().keySet().toArray();
                        String lastInsertedKey = (String)keys[oot.getLastFieldIndex()];
                        fields.put(lastInsertedKey, databaseType);
                    }
                }
            }
        }
        else {
            typeStack.push(databaseType);
        }
    }

    @Override
    public void beginObjectType(final String objectTypename) {
        DatabaseType databaseType = OraclePLSQLTypes.getDatabaseTypeForCode(
            trimOffSchemaName(objectTypename));
        if (databaseType == null) {
            databaseType = getKnownDatabaseType(objectTypename);
        }
        if (databaseType == null) {
            OracleObjectType objectType = new OracleObjectType();
            objectType.setTypeName(objectTypename);
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                if (cdt.isJDBCType()) {
                    if (cdt.isCollection()) {
                        OracleArrayType oat = (OracleArrayType)cdt;
                        oat.setNestedType(objectType);
                    }
                    else {
                        OracleObjectType oot = (OracleObjectType)cdt;
                        Map<String, DatabaseType> fields = oot.getFields();
                        Object[] keys = oot.getFields().keySet().toArray();
                        String lastInsertedKey = (String)keys[oot.getLastFieldIndex()];
                        if (fields.get(lastInsertedKey) == JDBCTypes.NULL_TYPE) {
                            // nested OracleObjectTypes
                            fields.put(lastInsertedKey, objectType);
                        }
                    }
                }
            }
            typeStack.push(objectType);
            putKnownDatabaseType(objectTypename, objectType);
        }
    }

    @Override
    public void handleObjectType(String objectTypename, String targetTypeName, int numAttributes) {
        DatabaseType databaseType = OraclePLSQLTypes.getDatabaseTypeForCode(
            trimOffSchemaName(objectTypename));
        if (databaseType == null) {
            databaseType = getKnownDatabaseType(objectTypename);
        }
        if (!typeStack.empty()) {
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                if (cdt.isCollection()) {
                    PLSQLCollection coll = (PLSQLCollection)cdt;
                    if (coll.getNestedType() == null) {
                        coll.setNestedType(databaseType);
                    }
                }
                else if (cdt.isRecord()) {
                    PLSQLrecord rec = (PLSQLrecord)cdt;
                    List<PLSQLargument> fields = rec.getFields();
                    PLSQLargument arg = fields.get(fields.size() - 1);
                    if (arg.databaseType == null) {
                        arg.databaseType = databaseType;
                    }
                }
                else if (cdt.isJDBCType()) {
                    if (typeStack.size() > 1) {
                        typeStack.pop();
                        DatabaseType topMinus1 = typeStack.peek();
                        if (topMinus1.isComplexDatabaseType()) {
                            ComplexDatabaseType cdtMinus1 = (ComplexDatabaseType)topMinus1;
                            if (cdtMinus1.isRecord()) {
                                PLSQLrecord rec = (PLSQLrecord)cdtMinus1;
                                List<PLSQLargument> fields = rec.getFields();
                                PLSQLargument arg = fields.get(fields.size() - 1);
                                if (arg.databaseType == null) {
                                    arg.databaseType = cdt;
                                }
                            }
                        }
                        typeStack.push(top);
                    }
                }
            }
        }
        else {
            typeStack.push(databaseType);
        }
    }

    @Override
    public void handleAttributeField(String attributeFieldName, int idx) {
        if (!typeStack.empty()) {
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType() && top.isJDBCType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                OracleObjectType oot = (OracleObjectType)cdt;
                oot.getFields().put(attributeFieldName, JDBCTypes.NULL_TYPE); // placeholder
                oot.setLastFieldIndex(idx);
            }
        }
    }

    @Override
    public void endObjectType(String objectTypename) {
        DatabaseType databaseType = OraclePLSQLTypes.getDatabaseTypeForCode(
           trimOffSchemaName(objectTypename));
        if (databaseType == null) {
            DatabaseType top = typeStack.peek();
            if (top.getTypeName().equals(objectTypename)) {
                typeStack.pop();
            }
        }
    }

    @Override
    public void handleSqlArrayType(String name, String targetTypeName) {
        DatabaseType databaseType = OraclePLSQLTypes.getDatabaseTypeForCode(
            trimOffSchemaName(name));
        if (databaseType == null) {
            databaseType = getKnownDatabaseType(name);
        }
        if (databaseType == null) {
            OracleArrayType oat = new OracleArrayType();
            oat.setTypeName(name);
            typeStack.push(oat);
            putKnownDatabaseType(name, oat);
        }
        else {
            typeStack.push(databaseType);
        }
    }

    @Override
    public void endPlsqlRecordField(String fieldName, int idx) {
        if (!typeStack.empty()) {
            DatabaseType top = typeStack.peek();
            if (top.isComplexDatabaseType()) {
                ComplexDatabaseType cdt = (ComplexDatabaseType)top;
                if (cdt.isJDBCType() && cdt.isCollection()) {
                    // take OracleArrayType off stack
                    DatabaseType pop = typeStack.pop();
                    if (!typeStack.isEmpty()) {
                        DatabaseType topMinus1 = typeStack.peek();
                        if (topMinus1.isComplexDatabaseType()) {
                            ComplexDatabaseType cdtMinus1 = (ComplexDatabaseType)topMinus1;
                            if (cdtMinus1.isRecord()) {
                                PLSQLrecord recMinus1 = (PLSQLrecord)cdtMinus1;
                                PLSQLargument arg = null;
                                for (Iterator<PLSQLargument> i = recMinus1.getFields().iterator(); i.hasNext();) {
                                    arg = i.next();
                                    if (arg.name.equalsIgnoreCase(fieldName)) {
                                        break;
                                    }
                                }
                                if (arg != null && arg.databaseType == null) {
                                    arg.databaseType = pop;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
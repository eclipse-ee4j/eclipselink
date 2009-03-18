/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle.publisher.visit;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.AttributeField;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.ProcedureMethod;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.PlsqlRecordType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.PlsqlTableType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlObjectType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlPackageType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.TypeClass;

public class PublisherWalker implements PublisherVisitor {

    protected PublisherListener listener;
    
    public PublisherWalker(PublisherListener listener) {
        this.listener = listener;
    }

    public PublisherListener getListener() {
        return listener;
    }

    public void visit(SqlType sqlType) {
        String targetTypeName = null;
        if (sqlType.hasConversion()) {
            targetTypeName = sqlType.getTargetTypeName();
        }
        listener.handleSqlType(sqlType.getName(), sqlType.getTypecode(), targetTypeName);
    }

    public void visit(SqlObjectType sqlObjectType) {
        String targetTypeName = null;
        if (sqlObjectType.hasConversion()) {
            targetTypeName = sqlObjectType.getTargetTypeName();
        }
        listener.handleObjectType(sqlObjectType.getName(), targetTypeName);
    }
    
    public void visit(SqlPackageType sqlPackageType) {
        listener.beginPackage(sqlPackageType.getName());
        try {
            ProcedureMethod[] declaredMethods = sqlPackageType.getDeclaredMethods();
            for (ProcedureMethod m : declaredMethods) {
                m.accept(this);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        listener.endPackage();
    }
    
    public void visit(PlsqlRecordType plsqlRecordType) {
        String targetTypeName = null;
        if (plsqlRecordType.hasConversion()) {
            targetTypeName = plsqlRecordType.getTargetTypeName();
        }
        AttributeField[] fields = null;
        int fieldsLength = 0;
        try {
            fields = plsqlRecordType.getFields(true);
            fieldsLength = fields.length;
        }
        catch (Exception e) { /* ignore */ }
        listener.beginPlsqlRecord(plsqlRecordType.getTypeName(), targetTypeName, fieldsLength);
        if (fields != null && fieldsLength > 0) {
            for (int idx = 0; idx < fieldsLength; idx++) {
                AttributeField f = fields[idx];
                listener.beginPlsqlRecordField(f.getName(), idx);
                ((SqlType)f.getType()).accept(this);
                listener.endPlsqlRecordField(f.getName(), idx);
            }
        }
        listener.endPlsqlRecord(plsqlRecordType.getTypeName());
    }

    public void visit(PlsqlTableType plsqlTableType) {
        String targetTypeName = null;
        if (plsqlTableType.hasConversion()) {
            targetTypeName = plsqlTableType.getTargetTypeName();
        }
        listener.beginPlsqlTable(plsqlTableType.getTypeName(), targetTypeName);
        try {
            if (plsqlTableType.getComponentType() != null) {
                ((SqlType)plsqlTableType.getComponentType()).accept(this);
            }
        }
        catch (Exception e) { /* ignore */ }
        listener.endPlsqlTable(plsqlTableType.getTypeName());
    }

    public void visit(ProcedureMethod method) {
        TypeClass[] paramTypes = method.getParamTypes();
        int len = paramTypes.length;
        listener.beginMethod(method.getName(), len);
        SqlType returnType = (SqlType)method.getReturnType();
        if (returnType != null) {
            listener.handleMethodReturn(returnType.getName());
            returnType.accept(this);
        }
        String[] paramNames = method.getParamNames();
        int[] paramModes = method.getParamModes();
        for (int idx = 0; idx < len; idx++ ) {
            SqlType argType = (SqlType)paramTypes[idx];
            int mode = paramModes[idx];
            String modeStr = "IN";
            if (mode == ProcedureMethod.OUT) {
                modeStr = "OUT";
            }
            else if (mode == ProcedureMethod.INOUT) {
                modeStr = "INOUT";
            }
            listener.beginMethodArg(paramNames[idx], modeStr, idx);
            argType.accept(this);
            listener.endMethodArg(paramNames[idx]);
            
        }
        listener.endMethod(method.getName());
    }
}
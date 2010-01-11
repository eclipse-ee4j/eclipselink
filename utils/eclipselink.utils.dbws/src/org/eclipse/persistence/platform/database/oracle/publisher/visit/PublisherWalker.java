/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

//javase imports
import java.util.List;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.AttributeField;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.ProcedureMethod;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.PlsqlRecordType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.PlsqlTableType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlArrayType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlObjectType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlPackageType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlTableType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlToplevelType;
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
        int numAttributes = 0;
        List<AttributeField> fields = null;
        try {
            fields = sqlObjectType.getDeclaredFields(false);
            numAttributes = fields.size();
        }
        catch (Exception e) {
        }
        listener.handleObjectType(sqlObjectType.getName(), targetTypeName, numAttributes);
        if (numAttributes > 0) {
            for (AttributeField field : fields) {
                TypeClass typeClass = field.getType();
                listener.handleAttributeField(field.getName());
                ((SqlType)typeClass).accept(this);
            }
        }
    }
    
    public void visit(SqlArrayType sqlArrayType) {
        String targetTypeName = null;
        if (sqlArrayType.hasConversion()) {
            targetTypeName = sqlArrayType.getTargetTypeName();
        }
        listener.handleSqlArrayType(sqlArrayType.getName(), targetTypeName);
        try {
            TypeClass componentType = sqlArrayType.getComponentType();
            ((SqlType)componentType).accept(this);
        }
        catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void visit(SqlTableType sqlTableType) {
        String targetTypeName = null;
        if (sqlTableType.hasConversion()) {
            targetTypeName = sqlTableType.getTargetTypeName();
        }
        listener.handleSqlTableType(sqlTableType.getName(), targetTypeName);
        try {
            TypeClass componentType = sqlTableType.getComponentType();
            ((SqlType)componentType).accept(this);
        }
        catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void visit(SqlPackageType sqlPackageType) {
        listener.beginPackage(sqlPackageType.getName());
        try {
            List<ProcedureMethod> declaredMethods = sqlPackageType.getDeclaredMethods();
            for (ProcedureMethod m : declaredMethods) {
                m.accept(this);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        listener.endPackage();
    }
    
    public void visit(SqlToplevelType sqlToplevelType) {
        listener.beginPackage("toplevel");
        try {
            List<ProcedureMethod> declaredMethods = sqlToplevelType.getDeclaredMethods();
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
        List<AttributeField> fields = null;
        int fieldsLength = 0;
        try {
            fields = plsqlRecordType.getFields(true);
            fieldsLength = fields.size();
        }
        catch (Exception e) { /* ignore */ }
        listener.beginPlsqlRecord(plsqlRecordType.getTypeName(), targetTypeName, fieldsLength);
        if (fields != null && fieldsLength > 0) {
            for (int idx = 0; idx < fieldsLength; idx++) {
                AttributeField f = fields.get(idx);
                listener.beginPlsqlRecordField(f.getName(), idx);
                ((SqlType)f.getType()).accept(this);
                listener.endPlsqlRecordField(f.getName(), idx);
            }
        }
        try {
            listener.endPlsqlRecord(plsqlRecordType.getTypeName(),
                plsqlRecordType.getSqlTypeDecl(), plsqlRecordType.getSqlTypeDrop());
        }
        catch (Exception e) { /* ignore */ }
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
        try {
            listener.endPlsqlTable(plsqlTableType.getTypeName(),
                plsqlTableType.getSqlTypeDecl(), plsqlTableType.getSqlTypeDrop());
        }
        catch (Exception e) { /* ignore */ }
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
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
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

//Java extension library imports
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.publisher.MethodFilter;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Method;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Name;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.OracleTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlName;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflector;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlTypeWithMethods;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Type;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.Util;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredFunction;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;
import static org.eclipse.persistence.tools.dbws.Util.escapePunctuation;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;

public class OracleHelper {

    public static List<DbStoredProcedure> buildStoredProcedure(Connection connection,
        String username, DatabasePlatform platform, String originalCatalogPattern,
        String originalSchemaPattern, String originalProcedurePattern, DBWSBuilder builder) {

        List<DbStoredProcedure> dbStoredProcedures = null;
        String packageName = escapePunctuation(originalCatalogPattern, true);
        String schemaPattern = escapePunctuation(originalSchemaPattern, true);
        final String procedurePattern = escapePunctuation(originalProcedurePattern, true);
        if (schemaPattern == null || schemaPattern.length() == 0) {
            schemaPattern = username;
        }
        SqlReflector sqlReflector = new SqlReflector(connection,username);
        try {
            SqlTypeWithMethods typ = (SqlTypeWithMethods)sqlReflector.addSqlUserType(schemaPattern,
                packageName, 4, true, 0, 0, new MethodFilter() {
                    public boolean acceptMethod(Method method, boolean preApprove) {
                        String methodName = method.getName();
                        if (sqlMatch(procedurePattern, methodName)) {
                            return true;
                        }
                        return false;
                    }

                });
            Method[] methods = typ.getDeclaredMethods();
            if (methods.length > 0) {
                // save SqlTypeWithMethods for later processing
                builder.setSqlType(typ);
                dbStoredProcedures = new ArrayList<DbStoredProcedure>();
                for (Method m : methods) {
                    DbStoredProcedure dbStoredProcedure = null;
                    Type returnType = m.getReturnType();
                    if (returnType == null) {
                        dbStoredProcedure = new DbStoredProcedure(m.getName());
                    }
                    else {
                        dbStoredProcedure = new DbStoredFunction(m.getName());
                        DbStoredArgument dbStoredArgument = new DbStoredArgument(returnType.getName());
                        dbStoredArgument.setInOut(RETURN);
                        dbStoredArgument.setSeq(0);
                        dbStoredArgument.setJdbcType(returnType.getJdbcTypecode());
                        Name n = returnType.getNameObject();
                        String typeName;
                        if (n instanceof SqlName) {
                            typeName = ((SqlName)n).getTypeName();
                        }
                        else {
                            typeName = n.getSimpleName();
                        }
                        dbStoredArgument.setJdbcTypeName(typeName);
                        ((DbStoredFunction)dbStoredProcedure).setReturnArg(dbStoredArgument);
                    }
                    dbStoredProcedure.setCatalog(packageName);
                    dbStoredProcedure.setSchema(originalSchemaPattern);
                    for (int i = 0, l = m.getParamNames().length; i < l; i ++) {
                        String argName = m.getParamNames()[i];
                        Type parameterType = m.getParamTypes()[i];
                        DbStoredArgument dbStoredArgument = null;
                        if (parameterType.isPrimitive()) {
                            dbStoredArgument = new DbStoredArgument(argName);
                        }
                        else {
                            dbStoredArgument = new PLSQLStoredArgument(argName);
                        }
                        int mode = m.getParamModes()[i];
                        InOut inOut = IN;
                        if (mode == Method.OUT) {
                            inOut = OUT;
                        }
                        else if (mode == Method.INOUT) {
                            inOut = INOUT;
                        }
                        dbStoredArgument.setInOut(inOut);
                        dbStoredArgument.setSeq(i);
                        dbStoredArgument.setJdbcType(parameterType.getJdbcTypecode());
                        Name n = parameterType.getNameObject();
                        String typeName;
                        if (n instanceof SqlName) {
                            typeName = ((SqlName)n).getTypeName();
                        }
                        else {
                            typeName = n.getSimpleName();
                        }
                        if (parameterType.isPrimitive()) {
                            dbStoredArgument.setJdbcTypeName(typeName);
                        }
                        else {
                            PLSQLStoredArgument plSqlArg =  (PLSQLStoredArgument)dbStoredArgument;
                            plSqlArg.setPlSqlTypeName(typeName);
                            plSqlArg.setJdbcTypeName(n.getSimpleName());
                        }
                        dbStoredProcedure.getArguments().add(dbStoredArgument);
                    }
                    dbStoredProcedures.add(dbStoredProcedure);
                }
            }
        }
        catch (Exception e) { /* ignore */ }
        return dbStoredProcedures;
    }

    public static QName getXMLTypeFromJDBCType(DbStoredArgument arg, String targetNamespace) {
        if (OracleTypes.ARRAY == arg.getJdbcType() ||
            OracleTypes.STRUCT == arg.getJdbcType()) {
            return new QName(targetNamespace, arg.getJdbcTypeName());
        }
        else {
            return  Util.getXMLTypeFromJDBCType(arg.getJdbcType());
        }
    }
}

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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

//Java extension library imports
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.publisher.MethodFilter;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.ProcedureMethod;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Name;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.OracleTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlName;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflector;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlTypeWithMethods;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.TypeClass;
import org.eclipse.persistence.tools.dbws.ProcedureOperationModel;
import org.eclipse.persistence.tools.dbws.Util;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredFunction;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes.BinaryInteger;
import static org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes.PLSQLBoolean;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.IS_PACKAGE;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.IS_TOPLEVEL;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.TOPLEVEL;
import static org.eclipse.persistence.tools.dbws.Util.sqlMatch;
import static org.eclipse.persistence.tools.dbws.Util.escapePunctuation;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;

public class OracleHelper {

    public static List<DbStoredProcedure> buildStoredProcedure(Connection connection,
        String username, DatabasePlatform platform, ProcedureOperationModel procedureModel) {

        List<DbStoredProcedure> dbStoredProcedures = null;
        String originalCatalogPattern = procedureModel.getCatalogPattern();
        String originalSchemaPattern = procedureModel.getSchemaPattern();
        String originalProcedurePattern = procedureModel.getProcedurePattern();
        String packageName = escapePunctuation(originalCatalogPattern, true);
        String schemaPattern = escapePunctuation(originalSchemaPattern, true);
        final String procedurePattern = escapePunctuation(originalProcedurePattern, true);
        if (schemaPattern == null || schemaPattern.length() == 0) {
            schemaPattern = username;
        }
        SqlTypeWithMethods typ = null;
        if (procedureModel.getJPubType() == null) {
            SqlReflector sqlReflector = new SqlReflector(connection,username);
            int whatItIs = IS_PACKAGE;
            if (TOPLEVEL.equalsIgnoreCase(packageName)) {
                whatItIs = IS_TOPLEVEL;
                packageName = "";
            }
            try {
                typ = (SqlTypeWithMethods)sqlReflector.addSqlUserType(schemaPattern,
                    packageName, whatItIs, true, 0, 0, new MethodFilter() {
                        public boolean acceptMethod(ProcedureMethod method, boolean preApprove) {
                            String methodName = method.getName();
                            if (sqlMatch(procedurePattern, methodName)) {
                                return true;
                            }
                            return false;
                        }

                    }
                );
                procedureModel.setJPubType(typ);
            }
            catch (Exception e) {
                // TODO
            }
        }
        else {
            typ = procedureModel.getJPubType();
        }
        List<ProcedureMethod> methods = null;
        try {
            methods = typ.getDeclaredMethods();
        }
        catch (Exception e) {
            // TODO
        }
        if (methods.size() > 0) {
            dbStoredProcedures = new ArrayList<DbStoredProcedure>();
            for (ProcedureMethod m : methods) {
                DbStoredProcedure dbStoredProcedure = null;
                TypeClass returnType = m.getReturnType();
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
                    TypeClass parameterType = m.getParamTypes()[i];
                    boolean isJDBCType = parameterType.isPrimitive() || parameterType.isObject() ||
                        parameterType.isTable() || parameterType.isArray();
                    DbStoredArgument dbStoredArgument = null;
                    if (isJDBCType) {
                        dbStoredArgument = new DbStoredArgument(argName);
                    }
                    else {
                        dbStoredArgument = new PLSQLStoredArgument(argName);
                    }
                    int mode = m.getParamModes()[i];
                    InOut inOut = IN;
                    if (mode == ProcedureMethod.OUT) {
                        inOut = OUT;
                    }
                    else if (mode == ProcedureMethod.INOUT) {
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
                    if (isJDBCType) {
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
        return dbStoredProcedures;
    }

    public static QName getXMLTypeFromJDBCType(DbStoredArgument arg, String targetNamespace) {
        QName fromJDBCType = null;
        if (OracleTypes.ARRAY == arg.getJdbcType() ||
            OracleTypes.STRUCT == arg.getJdbcType()) {
            fromJDBCType = new QName(targetNamespace, arg.getJdbcTypeName());
        }
        else {
            int typ = arg.getJdbcType();
            if (arg.isPLSQLArgument()) {
                PLSQLStoredArgument plsqlArg = (PLSQLStoredArgument)arg;
                String plsqlTypeName = plsqlArg.getPlSqlTypeName();
                if ("BOOLEAN".equals(plsqlTypeName)) {
                    typ = PLSQLBoolean.getConversionCode();
                }
                else {
                    typ = BinaryInteger.getConversionCode();

                }
            }
            if ("LONG".equals(arg.getJdbcTypeName())) {
                fromJDBCType = BASE_64_BINARY_QNAME;
            }
            else {
                fromJDBCType = Util.getXMLTypeFromJDBCType(typ);
            }
        }
        return fromJDBCType;
    }
}

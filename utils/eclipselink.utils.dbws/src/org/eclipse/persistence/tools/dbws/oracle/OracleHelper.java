package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

//Java extension library imports
import javax.xml.namespace.QName;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.publisher.SqlReflector;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Method;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Name;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.OracleTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlName;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflectorImpl;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlTypeWithMethods;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Type;
import org.eclipse.persistence.tools.dbws.Util;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredFunction;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.tools.dbws.Util.trimPunctuation;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;

public class OracleHelper {

    public static List<DbStoredProcedure> buildStoredProcedure(Connection connection,
        String username, DatabasePlatform platform, String originalCatalogPattern,
        String originalSchemaPattern, String originalProcedurePattern) {

        List<DbStoredProcedure> dbStoredProcedures = null;
        String packageName = trimPunctuation(originalCatalogPattern, true);
        String schemaPattern = trimPunctuation(originalSchemaPattern, true);
        String procedurePattern = trimPunctuation(originalProcedurePattern, true);
        if (schemaPattern == null || schemaPattern.length() == 0) {
            schemaPattern = username;
        }
        SqlReflectorImpl sqlReflector = (SqlReflectorImpl)SqlReflector.SqlReflectorHelper.
            getDefaultSqlReflector(connection,username);
        try {
            SqlTypeWithMethods typ = (SqlTypeWithMethods)sqlReflector.addSqlUserType(schemaPattern,
                packageName, 4, true, 0, 0, null);
            boolean found = false;
            Method m = null;
            Method[] methods = typ.getDeclaredMethods();
            for (int i = 0, l = methods.length; i < l; i ++) {
                Method method = methods[i];
                if (method.getName().equalsIgnoreCase(procedurePattern)) {
                    found = true;
                    m = method;
                    break;
                }
            }
            if (found) {
                dbStoredProcedures = new ArrayList<DbStoredProcedure>();
                DbStoredProcedure dbStoredProcedure = null;
                Type returnType = m.getReturnType();
                if (returnType == null) {
                    dbStoredProcedure = new DbStoredProcedure(procedurePattern);
                }
                else {
                    dbStoredProcedure = new DbStoredFunction(procedurePattern);
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
        catch (Exception e) {
            e.printStackTrace();
        }
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

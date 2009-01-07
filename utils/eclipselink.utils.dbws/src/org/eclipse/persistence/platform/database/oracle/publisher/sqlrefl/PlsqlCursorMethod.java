/* 1998 (c) Oracle Corporation */
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.RowtypeInfo;

/**
 * A PL/SQL method returning REF CURSOR as beans
 */
public class PlsqlCursorMethod extends PlsqlMethod implements CursorMethod {
    public PlsqlCursorMethod(String packageName, String methodName, String methodNo, int modifiers,
        int sequence, Type[] parameterTypes, String[] parameterNames, int[] parameterModes,
        boolean[] parameterDefaults, int paramLen, boolean returnBeans, SqlReflectorImpl reflector)
        throws SQLException {
        super(methodName, null, /* overloadNumber */
        modifiers, SqlReflectorImpl.REF_CURSOR_TYPE, parameterTypes, parameterNames, parameterModes,
            parameterDefaults, paramLen);
        m_returnBeans = returnBeans;
        m_reflector = reflector;

        if (!m_returnBeans) {
            return;
        }

        /*
         * figure out returned bean type ARG_NAME DATA_TYPE POSITION SEQUENCE DATA_LEVEL JOB
         * VARCHAR2 3 5 2 ENAME VARCHAR2 2 4 2 EMPNO NUMBER 1 3 2 PL/SQL RECORD 1 2 1 <sequence + 1
         * REF CURSOR 0 1 0 <sequence
         */
        RowtypeInfo[] rowtypeInfo = m_reflector.reflectRowtypeInfo(packageName, methodName,
            methodNo, sequence + 1);
        m_returnColCount = rowtypeInfo.length;
        Type[] returnColTypes = new Type[m_returnColCount];
        String[] returnColNames = new String[m_returnColCount];
        for (int j = 0; j < m_returnColCount; j++) {
            returnColNames[j] = rowtypeInfo[j].argument_name;
            try {
                /*
                 * returnColTypes[j] = reflector.addSqlDBType( rowtypeInfo[j].type_owner,
                 * rowtypeInfo[j].data_type, rowtypeInfo[j].type_subname, null, false, null);
                 */
                returnColTypes[j] = reflector.addPlsqlDBType(rowtypeInfo[j].type_owner,
                    rowtypeInfo[j].type_name, rowtypeInfo[j].type_subname, rowtypeInfo[j].modifier, /* modifier */
                    false, /* ncharFormOfUse */
                    packageName, methodName, methodNo, rowtypeInfo[j].sequence, null);
            }
            catch (Exception e) {
                throw new SQLException(e.getMessage());
            }
        }
        String returnEleTypeName = Util.uniqueResultTypeName(SqlName
            .sqlIdToJavaId(methodName, true), "Row");
        Field[] fields = null;
        m_returnEleType = null;
        if (returnColTypes != null && returnColTypes.length == 1) {
            m_singleColName = returnColNames[0];
            m_returnEleType = returnColTypes[0];
        }
        else if (returnColTypes != null) {
            fields = new Field[returnColTypes.length];
            for (int i = 0; i < returnColTypes.length; i++) {
                // MYOBJ(ENAME, SAL) => MYOBJ
                String returnColName = returnColNames[i];
                if (returnColName.indexOf("(") > -1) {
                    returnColName = returnColName.substring(0, returnColName.indexOf("("));
                }
                fields[i] = new Field(returnColName, returnColTypes[i], 0, 0, 0,
                    null /* CHARACTER_SET_NAME */, m_reflector);
            }
            m_returnEleType = m_reflector.addJavaType(returnEleTypeName, fields, null, true, null);
        }
        else {
            m_returnEleType = m_reflector.addJavaType(returnEleTypeName, new Field[0], null, true,
                null);
        }
        m_returnType = new JavaArrayType(m_returnEleType, m_reflector, SqlReflectorImpl.REF_CURSOR_TYPE);
    }

    public Type getReturnEleType() {
        return m_returnEleType;
    }

    // 0 means a non-typed ref cursor
    public int getReturnColCount() {
        return m_returnColCount;
    }

    public boolean isSingleCol() {
        return m_singleColName != null;
    }

    public String singleColName() {
        return m_singleColName;
    }

    public boolean returnBeans() {
        return m_returnBeans;
    }

    public boolean returnResultSet() {
        return !m_returnBeans;
    }

    private SqlReflectorImpl m_reflector;
    private String m_singleColName;
    private Type m_returnEleType;
    private boolean m_returnBeans;
    private int m_returnColCount;
}

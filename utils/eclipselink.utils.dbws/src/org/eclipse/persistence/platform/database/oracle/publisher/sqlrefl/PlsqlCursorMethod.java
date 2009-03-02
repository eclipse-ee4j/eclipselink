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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.RowtypeInfo;

/**
 * A PL/SQL method returning REF CURSOR as beans
 */
public class PlsqlCursorMethod extends PlsqlMethod implements CursorMethod {
    public PlsqlCursorMethod(String packageName, String methodName, String methodNo, int modifiers,
        int sequence, TypeClass[] parameterTypes, String[] parameterNames, int[] parameterModes,
        boolean[] parameterDefaults, int paramLen, boolean returnBeans, SqlReflector reflector)
        throws SQLException {
        super(methodName, null, /* overloadNumber */
        modifiers, SqlReflector.REF_CURSOR_TYPE, parameterTypes, parameterNames, parameterModes,
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
        TypeClass[] returnColTypes = new TypeClass[m_returnColCount];
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
        AttributeField[] fields = null;
        m_returnEleType = null;
        if (returnColTypes != null && returnColTypes.length == 1) {
            m_singleColName = returnColNames[0];
            m_returnEleType = returnColTypes[0];
        }
        else if (returnColTypes != null) {
            fields = new AttributeField[returnColTypes.length];
            for (int i = 0; i < returnColTypes.length; i++) {
                // MYOBJ(ENAME, SAL) => MYOBJ
                String returnColName = returnColNames[i];
                if (returnColName.indexOf("(") > -1) {
                    returnColName = returnColName.substring(0, returnColName.indexOf("("));
                }
                fields[i] = new AttributeField(returnColName, returnColTypes[i], 0, 0, 0,
                    null /* CHARACTER_SET_NAME */, m_reflector);
            }
            m_returnEleType = m_reflector.addJavaType(returnEleTypeName, fields, null, true, null);
        }
        else {
            m_returnEleType = m_reflector.addJavaType(returnEleTypeName, new AttributeField[0], null, true,
                null);
        }
        m_returnType = new JavaArrayType(m_returnEleType, m_reflector, SqlReflector.REF_CURSOR_TYPE);
    }

    public TypeClass getReturnEleType() {
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

    private SqlReflector m_reflector;
    private String m_singleColName;
    private TypeClass m_returnEleType;
    private boolean m_returnBeans;
    private int m_returnColCount;
}

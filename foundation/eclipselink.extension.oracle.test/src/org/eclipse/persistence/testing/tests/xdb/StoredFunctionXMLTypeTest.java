/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.xdb;

import java.sql.Types;
import java.util.List;

import oracle.jdbc.OracleTypes;
import oracle.sql.OPAQUE;

import org.eclipse.persistence.internal.helper.JavaPlatform;
import org.eclipse.persistence.internal.platform.database.oracle.XMLTypeFactory;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.StoredFunctionCall;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class StoredFunctionXMLTypeTest extends TestCase {
    public static String charsToIgnore = " \n\t";
    public void test() throws Exception {
        // 12.1 works with both OracleTypes.OPAQUE and Types.SQLXML
        // 11.2.0.3 - only with OracleTypes.OPAQUE
        // 11.2.0.2 - with both OracleTypes.OPAQUE and Types.SQLXML
//        int sqlType = Types.SQLXML;
        int sqlType = OracleTypes.OPAQUE;
        // see the stored function definition in XMLTypeEmployeeSystem
        StoredFunctionCall call = new StoredFunctionCall(sqlType, "XMLTYPE", String.class);
        call.setProcedureName("STOREDFUNCTION_XMLTYPE");
        if (sqlType == OracleTypes.OPAQUE && (getAbstractSession().isClientSession() || ((DatabaseLogin)getAbstractSession().getDatasourceLogin()).shouldUseExternalConnectionPooling())) {
            // UnwrapConnectionXDBTestModel uses external connection pooling. In this case transaction is required to keep the same connection open until the string is extracted.
            getAbstractSession().beginTransaction();
        }
        try {
            List result = getSession().executeSelectingCall(call);
            Object xmlResult = ((AbstractRecord)result.get(0)).getValues().get(0);

            String str;
            if (xmlResult instanceof OPAQUE) {
                str = ((XMLTypeFactory)Class.forName("org.eclipse.persistence.internal.platform.database.oracle.xdb.XMLTypeFactoryImpl").newInstance()).getString((OPAQUE)xmlResult);
            } else {
                str = JavaPlatform.getStringAndFreeSQLXML(xmlResult);
            }
            StringBuffer strBuffer = new StringBuffer();
            for (int i=0; i<str.length(); i++) {
                char ch = str.charAt(i);
                if (charsToIgnore.indexOf(ch) == -1) {
                    strBuffer.append(ch);
                }
            }
            String strWithoutSpaces = strBuffer.toString();

            if (!strWithoutSpaces.equals("<jb><data>BLAH</data></jb>")) {
                throw new TestErrorException("unexpected string: " + str);
            }
        } finally {
            if (getAbstractSession().isInTransaction()) {
                getAbstractSession().rollbackTransaction();
            }
        }
    }
}

/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//EclipseLink imports
import static dbws.testing.shadowddlgeneration.oldjpub.Util.MAX_IDENTIFIER_LENGTH;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.getDefaultTypeLen;

public class PlsqlIndexTableType extends SqlType {

    protected int m_elemTypecode;
    protected int m_maxLen;
    protected int m_maxElemLen;

    public PlsqlIndexTableType(SqlName sqlName, boolean isNumeric) {
        super(sqlName, OracleTypes.PLSQL_INDEX_TABLE);
        m_maxLen = MAX_IDENTIFIER_LENGTH;
        if (isNumeric) {
            m_elemTypecode = OracleTypes.NUMERIC;
            m_maxElemLen = 0;
        }
        else {
            m_elemTypecode = OracleTypes.CHAR;
            m_maxElemLen = Integer.valueOf(getDefaultTypeLen("VARCHAR2"));

        }
    }

    public PlsqlIndexTableType(SqlName sqlName, boolean isNumeric, int maxLen, int maxElemLen) {
        super(sqlName, OracleTypes.PLSQL_INDEX_TABLE);
        m_elemTypecode = isNumeric ? OracleTypes.NUMERIC : OracleTypes.CHAR;
        m_maxLen = maxLen;
        m_maxElemLen = maxElemLen;
    }

    public int getElemTypecode() {
        return m_elemTypecode;
    }

    public int getMaxLen() {
        return m_maxLen;
    }

    public int getMaxElemLen() {
        return m_maxElemLen;
    }
}

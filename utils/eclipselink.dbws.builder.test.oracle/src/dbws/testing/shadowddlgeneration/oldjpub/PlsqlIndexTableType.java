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

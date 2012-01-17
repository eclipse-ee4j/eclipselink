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

public class SqlSqljType extends SqlType {

    protected int m_kind;

    public SqlSqljType(SqlName sqlName, int kind, SqlType parentType, SqlReflector reflector) {
        super(sqlName, OracleTypes.JAVA_STRUCT, false, parentType, reflector);
        m_kind = kind;
    }

    public int getSqljKind() {
        return m_kind;
    }
}

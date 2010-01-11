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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;

/**
 * A Method provides information about a single method this method executes a SQL statement
 */
public class DMLMethod extends SqlStmtMethod {

    /**
     * Construct a Method that is a DML (which returns an int)
     * <p/>
     * m_isBatched <= isBatched
     */
    public DMLMethod(String name, int modifiers, String sqlStmt, boolean isBatched,
        SqlReflector reflector) throws SQLException,
        org.eclipse.persistence.platform.database.oracle.publisher.PublisherException {
        super(name, modifiers, sqlStmt, reflector);
        m_returnType = new JavaBaseType("int", null, null, SqlReflector.INT_TYPE);
        m_isBatched = isBatched;
        if (isBatched) {
            TypeClass[] batchParamTypes = new TypeClass[m_paramTypes.length];
            for (int i = 0; i < m_paramTypes.length; i++) {
                batchParamTypes[i] = new JavaArrayType((SqlType)m_paramTypes[i], m_reflector, null);
            }
            m_paramTypes = batchParamTypes;
        }
    }

    // A DML that is batched
    public boolean isBatched() {
        return m_isBatched;
    }

    public static boolean canDMLBatch(String[] dmlBatchOff, TypeClass[] params) {
        if (dmlBatchOff != null) {
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < dmlBatchOff.length; j++) {
                    String offName = dmlBatchOff[j].toUpperCase();
                    String paramName = params[i].getName().toUpperCase();
                    if (offName.equals(paramName) || paramName.endsWith(offName)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    boolean m_isBatched; // This DML method is batched or not
}

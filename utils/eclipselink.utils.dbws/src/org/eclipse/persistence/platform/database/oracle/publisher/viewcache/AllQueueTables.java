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
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

//javase imports
import java.sql.ResultSet;
import java.sql.SQLException;

// Includes all the columns in ALL_ARGUMENTS and USER_AUGUMENTS

public class AllQueueTables extends ViewRowFactory implements ViewRow {

    public static int iQUEUE_TABLE;
    public static int iOBJECT_TYPE;
    public static int iRECIPIENTS;
    public static int iOWNER;
    private static boolean m_indexed = false;

    // Attributes
    public String QUEUE_TABLE;
    public String OBJECT_TYPE; // DO _NOT_ REFACTOR THIS!
    public String RECIPIENTS; // DO _NOT_ REFACTOR THIS!
    public String OWNER; // DO _NOT_ REFACTOR THIS!

    protected AllQueueTables() {
        super();
    }

    public AllQueueTables(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iQUEUE_TABLE = rs.findColumn("QUEUE_TABLE");
            iOBJECT_TYPE = rs.findColumn("OBJECT_TYPE");
            iRECIPIENTS = rs.findColumn("RECIPIENTS");
            iOWNER = rs.findColumn("OWNER");
        }
        QUEUE_TABLE = rs.getString(iQUEUE_TABLE);
        OBJECT_TYPE = rs.getString(iOBJECT_TYPE);
        RECIPIENTS = rs.getString(iRECIPIENTS);
        OWNER = rs.getString(iOWNER);
    }

    @Override
    public boolean isAllQueueTables() {
        return true;
    }

    public String toString() {
        return QUEUE_TABLE + "," + OBJECT_TYPE + "," + RECIPIENTS + "," + OWNER;
    }

    public static String[] getProjectList() {
        return new String[]{"QUEUE_TABLE", "OBJECT_TYPE", "RECIPIENTS", "OWNER",};
    }
}

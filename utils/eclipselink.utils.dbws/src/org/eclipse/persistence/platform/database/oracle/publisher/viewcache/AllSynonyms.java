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

//EclipseLink imports
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.OWNER;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.SYNONYM_NAME;

public class AllSynonyms extends ViewRowFactory implements ViewRow {

    public static int iOWNER;
    public static int iSYNONYM_NAME;
    private static boolean m_indexed = false;

    // Attributes
    public String owner;
    public String synonymName;

    public AllSynonyms(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iSYNONYM_NAME = rs.findColumn(SYNONYM_NAME);
            iOWNER = rs.findColumn(OWNER);
        }
        synonymName = rs.getString(iSYNONYM_NAME);
        owner = rs.getString(iOWNER);
    }

    @Override
    public boolean isAllSynonyms() {
        return true;
    }

    public String toString() {
        return owner + "," + synonymName;
    }

    public static String[] getProjectList() {
        return new String[]{"OWNER", "SYNONYM_NAME"};
    }
}

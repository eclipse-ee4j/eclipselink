/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.ResultSet;
import java.sql.SQLException;

//EclipseLink imports
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OWNER;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.SYNONYM_NAME;

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

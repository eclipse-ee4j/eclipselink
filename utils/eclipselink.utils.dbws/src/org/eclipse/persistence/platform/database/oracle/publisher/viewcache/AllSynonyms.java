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
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;

public class AllSynonyms extends ViewRowFactory implements ViewRow {
    // Attributes
    public String OWNER;
    public String SYNONYM_NAME;

    public static int iOWNER;
    public static int iSYNONYM_NAME;
    private static boolean m_indexed = false;

    public AllSynonyms(ResultSet rs) throws java.sql.SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iSYNONYM_NAME = rs.findColumn(Util.SYNONYM_NAME);
            iOWNER = rs.findColumn(Util.OWNER);
        }
        SYNONYM_NAME = rs.getString(iSYNONYM_NAME);
        OWNER = rs.getString(iOWNER);
    }

    public String toString() {
        return OWNER + "," + SYNONYM_NAME;
    }

    public static String[] getProjectList() {
        return new String[]{"OWNER", "SYNONYM_NAME"};
    }
}

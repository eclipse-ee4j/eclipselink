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

// Includes all the columns in ALL_TYPE_METHODS

public class AllObjects extends ViewRowFactory implements ViewRow {

    public static int iOBJECT_NAME = -1;
    public static int iOBJECT_TYPE = -1;

    // Attributes
    public String objectName;
    public String objectType;

    public AllObjects(ResultSet rset) throws SQLException {
        super();
        if (iOBJECT_TYPE == -1) {
            iOBJECT_TYPE = rset.findColumn("OBJECT_TYPE");
            iOBJECT_NAME = rset.findColumn("OBJECT_NAME");
        }
        objectName = rset.getString(iOBJECT_NAME);
        objectType = rset.getString(iOBJECT_TYPE);
    }

    @Override
    public boolean isAllObjects() {
        return true;
    }

    public String toString() {
        return objectName + "," + objectType;
    }

    public static String[] getProjectList() {
        return new String[]{"OBJECT_NAME", "OBJECT_TYPE"};
    }
}

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

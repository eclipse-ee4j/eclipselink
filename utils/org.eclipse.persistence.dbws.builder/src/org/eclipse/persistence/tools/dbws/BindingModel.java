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
//     Mike Norman - May 01 2008, created DBWS tools package

package org.eclipse.persistence.tools.dbws;

import java.util.ArrayList;

// Javase imports

// Java extension imports

// EclipseLink imports

public class BindingModel {

    public String name;
    public String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static String convertJDBCParameterBindingMarkers(String sqlString, ArrayList<BindingModel> bindings) {
        int idx = sqlString.indexOf("?");
        if (idx == -1) {
            return sqlString;
        }
        int argNumber = 0;
        int anchorIdx = 0;
        StringBuilder sb = new StringBuilder();
        while (idx != -1) {
            sb.append(sqlString.substring(anchorIdx, idx));
            sb.append("#");
            sb.append(bindings.get(argNumber).getName());
            anchorIdx = idx + 1;
            idx = sqlString.indexOf("?", anchorIdx);
            argNumber++;
            // bug 309002 - may be GROUP BY/ORDER BY clauses after last marker
            if (idx == -1) {
                sb.append(sqlString.substring(anchorIdx, sqlString.length()));
            }
        }
        return sb.toString();
    }

}

/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

import java.util.ArrayList;

// Javase imports

// Java extension imports

// EclipseLink imports

public class BindingModel {

    protected String name;
    protected String type;

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
        }
        return sb.toString();
    }

}

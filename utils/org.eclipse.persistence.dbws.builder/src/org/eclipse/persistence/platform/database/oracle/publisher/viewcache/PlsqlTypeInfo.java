/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.sql.SQLException;
import java.util.ArrayList;

public class PlsqlTypeInfo {

    public int sequence;
    public int position;
    public int dataLevel;
    public String argumentName;
    public String objectName;

    public PlsqlTypeInfo(UserArguments r) {
        sequence = r.sequence;
        position = r.POSITION;
        dataLevel = r.DATA_LEVEL;
        argumentName = r.ARGUMENT_NAME;
        objectName = r.OBJECT_NAME;
    }

    public static PlsqlTypeInfo[] getPlsqlTypeInfo(ArrayList<ViewRow> viewRows) throws SQLException {
        ArrayList<PlsqlTypeInfo> a = new ArrayList<PlsqlTypeInfo>();
        for (int i = 0; i < viewRows.size(); i++) {
            a.add(new PlsqlTypeInfo((UserArguments)viewRows.get(i)));
        }
        if (a.size() == 0) {
            return null;
        }
        return a.toArray(new PlsqlTypeInfo[a.size()]);
    }
}
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

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class PlsqlTypeInfo {
    public PlsqlTypeInfo(UserArguments r) throws java.sql.SQLException {
        sequence = r.sequence;
        position = r.POSITION;
        dataLevel = r.DATA_LEVEL;
        argumentName = r.ARGUMENT_NAME;
        objectName = r.OBJECT_NAME;
    }

    public int sequence;
    public int position;
    public int dataLevel;
    public String argumentName;
    public String objectName;

    public static PlsqlTypeInfo[] getPlsqlTypeInfo(ArrayList viewRows) throws java.sql.SQLException {
        ArrayList a = new ArrayList();
        for (int i = 0; i < viewRows.size(); i++) {
            a.add(new PlsqlTypeInfo((UserArguments)viewRows.get(i)));
        }
        if (a.size() == 0) {
            return null;
        }
        PlsqlTypeInfo[] r = new PlsqlTypeInfo[a.size()];
        for (int i = 0; i < a.size(); i++) {
            r[i] = (PlsqlTypeInfo)a.get(i);
        }
        return r;
    }
}

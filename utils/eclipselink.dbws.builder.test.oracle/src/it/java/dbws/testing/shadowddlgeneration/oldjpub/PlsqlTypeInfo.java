/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
        ArrayList<PlsqlTypeInfo> a = new ArrayList<>();
        for (ViewRow viewRow : viewRows) {
            a.add(new PlsqlTypeInfo((UserArguments) viewRow));
        }
        if (a.isEmpty()) {
            return null;
        }
        return a.toArray(new PlsqlTypeInfo[0]);
    }
}

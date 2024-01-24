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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MethodInfo {

    public String methodName;
    public String methodNo;
    public String methodType;
    public int parameters;
    public int results;

    public MethodInfo(UserArguments row) throws SQLException {
        methodName = row.OBJECT_NAME;
        methodNo = row.OVERLOAD;
        methodType = "PUBLIC";
        if (row.sequence == 0) {
            parameters = 0;
        }
        else {
            parameters = row.POSITION;
        }
        if (parameters < 0) {
            parameters = 0;
        }
        results = 1 - row.POSITION;
        if (results < 0) {
            results = 0;
        }
    }

    public MethodInfo(AllTypeMethods row) throws SQLException {
        methodName = row.methodName;
        methodNo = row.methodNo;
        methodType = row.methodType;
        parameters = row.parameters;
        results = row.results;
    }

    public static MethodInfo[] getMethodInfo(Iterator<ViewRow> iter) throws SQLException {
        ArrayList<MethodInfo> a = new ArrayList<>();
        while (iter.hasNext()) {
            ViewRow vr = iter.next();
            if (vr.isAllArguments() || vr.isUserArguments()) {
                a.add(new MethodInfo((AllArguments)vr));
            }
            else if (vr.isAllTypeMethods()) {
                a.add(new MethodInfo((AllTypeMethods)vr));
            }
        }
        return a.toArray(new MethodInfo[0]);
    }

    // GROUP BY: METHOD_NAME, METHOD_NO: MAX(PARAMETERS). MAX(RESULTS)
    public static MethodInfo[] groupBy(Iterator<ViewRow> iter) throws java.sql.SQLException {
        MethodInfo[] minfo = getMethodInfo(iter);
        Map<String,MethodInfo> miMap = new HashMap<>();
        for (MethodInfo methodInfo : minfo) {
            String key = methodInfo.methodName + "," + methodInfo.methodNo;
            MethodInfo mi = miMap.get(key);
            if (mi == null) {
                miMap.put(key, methodInfo);
            }
            if (mi != null && mi.parameters < methodInfo.parameters) {
                mi.parameters = methodInfo.parameters;
            }
            if (mi != null && mi.results < methodInfo.results) {
                mi.results = methodInfo.results;
            }
        }
        return miMap.values().toArray(new MethodInfo[0]);
    }
}

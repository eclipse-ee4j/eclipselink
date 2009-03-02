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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class MethodInfo {
    public MethodInfo(UserArguments row) throws java.sql.SQLException {
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

    public MethodInfo(AllTypeMethods row) throws java.sql.SQLException {
        methodName = row.methodName;
        methodNo = row.methodNo;
        methodType = row.methodType;
        parameters = row.parameters;
        results = row.results;
    }

    public static MethodInfo[] getMethodInfo(Iterator iter) throws java.sql.SQLException {
        Vector a = new Vector();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof AllArguments) {
                a.addElement(new MethodInfo((AllArguments)obj));
            }
            else if (obj instanceof UserArguments) {
                a.addElement(new MethodInfo((UserArguments)obj));
            }
            else {
                a.addElement(new MethodInfo((AllTypeMethods)obj));
            }
        }
        MethodInfo[] r = new MethodInfo[a.size()];
        for (int i = 0; i < a.size(); i++) {
            r[i] = (MethodInfo)a.elementAt(i);
        }
        return r;
    }

    // GROUP BY: METHOD_NAME, METHOD_NO: MAX(PARAMETERS). MAX(RESULTS)
    public static MethodInfo[] groupBy(Iterator iter) throws java.sql.SQLException {
        MethodInfo[] minfo = getMethodInfo(iter);
        Hashtable ht = new Hashtable();
        for (int i = 0; i < minfo.length; i++) {
            String key = minfo[i].methodName + "," + minfo[i].methodNo;
            MethodInfo mi = (MethodInfo)ht.get(key);
            if (mi == null) {
                ht.put(key, minfo[i]);
            }
            if (mi != null && mi.parameters < minfo[i].parameters) {
                mi.parameters = minfo[i].parameters;
            }
            if (mi != null && mi.results < minfo[i].results) {
                mi.results = minfo[i].results;
            }
        }
        minfo = new MethodInfo[ht.size()];
        Enumeration htEnum = ht.elements();
        int i = 0;
        while (htEnum.hasMoreElements()) {
            minfo[i++] = (MethodInfo)htEnum.nextElement();
        }
        return minfo;
    }

    public String methodName;
    public String methodNo;
    public String methodType;
    public int parameters;
    public int results;
}

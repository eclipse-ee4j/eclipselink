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

import java.util.Iterator;
import java.util.Vector;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflector;

@SuppressWarnings("unchecked")
public class ResultInfo {
    public ResultInfo(AllMethodResults r) throws java.sql.SQLException {
        resultTypeMod = r.resultTypeMod;
        resultTypeOwner = r.resultTypeOwner;
        resultTypeName = r.resultTypeName;
        if ("PL/SQL BINARY INTEGER".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "BINARY_INTEGER";
        }
        else if ("PL/SQL BOOLEAN".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "BOOLEAN";
        }
        else if ("PL/SQL PLS INTEGER".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "PLS_INTEGER";
        }
        else if ("PL/SQL LONG".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "LONG";
        }
        else if ("PL/SQL LONG RAW".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "LONG RAW";
        }
        else if ("PL/SQL RAWID".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "PL/SQL RAWID";
        }
        else if ("PL/SQL URAWID".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "PL/SQL URAWID";
        }
        resultTypeSubname = null;
        ncharFormOfUse = false;
        if (SqlReflector.NCHAR_CS.equals(r.characterSetName)) {
            ncharFormOfUse = true;
        }
        methodName = r.methodName;
        methodNo = r.methodNo;
        sequence = -1;
        dataLength = 0;
        dataPrecision = 0;
        dataScale = 0;
    }

    public ResultInfo(UserArguments r) throws java.sql.SQLException {
        resultTypeMod = ""; // null in SqlPackageType
        if ("REF".equalsIgnoreCase(r.DATA_TYPE) || "PL/SQL RECORD".equalsIgnoreCase(r.DATA_TYPE)
            || "PL/SQL TABLE".equalsIgnoreCase(r.DATA_TYPE)
            || "VARRAY".equalsIgnoreCase(r.DATA_TYPE) || "TABLE".equalsIgnoreCase(r.DATA_TYPE)) {
            resultTypeMod = r.DATA_TYPE;
        }
        resultTypeOwner = r.TYPE_OWNER;
        if ("PUBLIC".equalsIgnoreCase(resultTypeOwner)) {
            resultTypeOwner = "SYS";
        }
        resultTypeName = r.TYPE_NAME;
        if (SqlReflector.isNull(resultTypeName)) {
            resultTypeName = r.PLS_TYPE; // added fro SqlPackageType
        }
        if (SqlReflector.isNull(resultTypeName)) {
            resultTypeName = r.DATA_TYPE;
        }
        if ("PL/SQL BOOLEAN".equalsIgnoreCase(resultTypeName)) {
            resultTypeName = "BOOLEAN";
        }
        resultTypeSubname = r.TYPE_SUBNAME;
        ncharFormOfUse = false;
        if (SqlReflector.NCHAR_CS.equals(r.CHARACTER_SET_NAME)) {
            ncharFormOfUse = true;
        }
        methodName = r.OBJECT_NAME;
        methodNo = r.OVERLOAD;
        sequence = r.sequence;
        dataLength = r.DATA_LENGTH;
        dataPrecision = r.DATA_PRECISION;
        dataScale = r.DATA_SCALE;
    }

    public static ResultInfo getResultInfo(Iterator iter) throws java.sql.SQLException {
        Vector a = new Vector();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof UserArguments) {
                a.addElement(new ResultInfo((UserArguments)obj));
            }
            else {
                a.addElement(new ResultInfo((AllMethodResults)obj));
            }
        }
        ResultInfo[] r = new ResultInfo[a.size()];
        for (int i = 0; i < a.size(); i++) {
            r[i] = (ResultInfo)a.elementAt(i);
        }
        if (r.length == 0) {
            throw new java.sql.SQLException("Exhausted ResultSet in ResultInfo");
        }
        return r[0];
    }

    public String resultTypeName;
    public String resultTypeSubname;
    public boolean ncharFormOfUse;
    public String resultTypeOwner;
    public String resultTypeMod;
    public String methodName;
    public String methodNo;
    public int sequence;
    public int dataLength;
    public int dataPrecision;
    public int dataScale;
}

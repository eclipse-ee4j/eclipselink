/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.Iterator;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflector;

public class ResultInfo {

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

    public ResultInfo(AllMethodResults r) throws SQLException {
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

    public ResultInfo(UserArguments r) throws SQLException {
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

    public static ResultInfo getResultInfo(Iterator<ViewRow> iter) throws SQLException {
        ArrayList<ResultInfo> a = new ArrayList<ResultInfo>();
        while (iter.hasNext()) {
            ViewRow vr = iter.next();
            if (vr.isUserArguments() || vr.isAllArguments()) {
                a.add(new ResultInfo((UserArguments)vr));
            }
            else if (vr.isAllMethodResults()) {
                a.add(new ResultInfo((AllMethodResults)vr));
            }
        }
        if (a.size() == 0) {
            throw new SQLException("Exhausted ResultSet in ResultInfo");
        }
        return a.get(0);
    }
}
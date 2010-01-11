/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

public class ParamInfo {

    public String paramName;
    public String paramMode;
    public int paramNo;
    public String paramTypeName;
    public String paramTypeSubname;
    public boolean ncharFormOfUse;
    public String paramTypeOwner;
    public String paramTypeMod;
    public String methodName;
    public String methodNo;
    public int sequence;
    public int dataLength;
    public int dataPrecision;
    public int dataScale;
    public int objectId;

    public ParamInfo(AllMethodParams r) throws SQLException {
        paramName = r.paramName;
        paramMode = r.paramMode;
        paramTypeMod = r.paramTypeMod;
        paramTypeOwner = r.paramTypeOwner;
        paramTypeName = r.paramTypeName;
        if ("PL/SQL BINARY INTEGER".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "BINARY_INTEGER";
        }
        else if ("PL/SQL BOOLEAN".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "BOOLEAN";
        }
        else if ("PL/SQL PLS INTEGER".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "PLS_INTEGER";
        }
        else if ("PL/SQL LONG".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "LONG";
        }
        else if ("PL/SQL LONG RAW".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "LONG RAW";
        }
        else if ("PL/SQL RAWID".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "RAWID";
        }
        else if ("PL/SQL URAWID".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "URAWID";
        }
        paramTypeSubname = null;
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

    public ParamInfo(UserArguments r) throws SQLException {
        paramName = r.ARGUMENT_NAME;
        paramNo = r.POSITION;
        paramMode = r.IN_OUT;
        if ("IN/OUT".equalsIgnoreCase(paramMode)) {
            paramMode = "IN OUT";
        }
        paramTypeMod = ""; // null in SqlPackageType
        if ("REF".equalsIgnoreCase(r.DATA_TYPE) || "PL/SQL RECORD".equalsIgnoreCase(r.DATA_TYPE)
            || "PL/SQL TABLE".equalsIgnoreCase(r.DATA_TYPE)
            || "VARRAY".equalsIgnoreCase(r.DATA_TYPE) || "TABLE".equalsIgnoreCase(r.DATA_TYPE)) {
            paramTypeMod = r.DATA_TYPE;
        }
        paramTypeOwner = r.TYPE_OWNER;
        if ("PUBLIC".equalsIgnoreCase(paramTypeOwner)) {
            paramTypeOwner = "SYS";
        }

        paramTypeName = r.TYPE_NAME;
        if (SqlReflector.isNull(paramTypeName)) {
            paramTypeName = r.PLS_TYPE;
        }
        if (SqlReflector.isNull(paramTypeName)) {
            paramTypeName = r.DATA_TYPE;
        }
        if ("PL/SQL BOOLEAN".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "BOOLEAN";
        }
        paramTypeSubname = r.TYPE_SUBNAME;
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
        objectId = r.OBJECT_ID;
    }

    public static ParamInfo[] getParamInfo(Iterator<ViewRow> iter) throws SQLException {
        ArrayList<ViewRow> a = new ArrayList<ViewRow>();
        while (iter.hasNext()) {
            a.add(iter.next());
        }
        return getParamInfo(a);
    }

    public static ParamInfo[] getParamInfo(ArrayList<ViewRow> v) throws SQLException {
        ArrayList<ParamInfo> a = new ArrayList<ParamInfo>();
        for (int i = 0; i < v.size(); i++) {
            ViewRow vr = v.get(i);
            if (vr.isAllMethodParams()) {
                a.add(new ParamInfo((AllMethodParams)vr));
            }
            else if (vr.isUserArguments() || vr.isAllArguments()) {
                a.add(new ParamInfo((UserArguments)vr));
            }
        }
        return a.toArray(new ParamInfo[a.size()]);
    }
}
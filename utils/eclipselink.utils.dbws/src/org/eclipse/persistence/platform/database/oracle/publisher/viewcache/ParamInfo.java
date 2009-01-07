package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflectorImpl;

@SuppressWarnings("unchecked")
public class ParamInfo {
    public ParamInfo(AllMethodParams r) throws java.sql.SQLException {
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
        if (SqlReflectorImpl.NCHAR_CS.equals(r.characterSetName)) {
            ncharFormOfUse = true;
        }
        methodName = r.methodName;
        methodNo = r.methodNo;
        sequence = -1;
        dataLength = 0;
        dataPrecision = 0;
        dataScale = 0;
    }

    public ParamInfo(UserArguments r) throws java.sql.SQLException {
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
        if (SqlReflectorImpl.isNull(paramTypeName)) {
            paramTypeName = r.PLS_TYPE;
        }
        if (SqlReflectorImpl.isNull(paramTypeName)) {
            paramTypeName = r.DATA_TYPE;
        }
        if ("PL/SQL BOOLEAN".equalsIgnoreCase(paramTypeName)) {
            paramTypeName = "BOOLEAN";
        }
        paramTypeSubname = r.TYPE_SUBNAME;
        ncharFormOfUse = false;
        if (SqlReflectorImpl.NCHAR_CS.equals(r.CHARACTER_SET_NAME)) {
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

    public static ParamInfo[] getParamInfo(Iterator iter) throws java.sql.SQLException {
        ArrayList a = new ArrayList();
        while (iter.hasNext()) {
            a.add(iter.next());
        }
        return getParamInfo(a);
    }

    public static ParamInfo[] getParamInfo(ArrayList v) throws java.sql.SQLException {
        ArrayList a = new ArrayList();
        for (int i = 0; i < v.size(); i++) {
            Object obj = v.get(i);
            if (obj instanceof AllMethodParams) {
                a.add(new ParamInfo((AllMethodParams)obj));
            }
            else {
                a.add(new ParamInfo((UserArguments)obj));
            }
        }
        ParamInfo[] r = new ParamInfo[a.size()];
        for (int i = 0; i < a.size(); i++) {
            r[i] = (ParamInfo)a.get(i);
        }
        return r;
    }

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
}

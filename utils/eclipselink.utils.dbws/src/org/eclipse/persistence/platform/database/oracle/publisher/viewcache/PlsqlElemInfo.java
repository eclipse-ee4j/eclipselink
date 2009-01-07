package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

public class PlsqlElemInfo extends ElemInfo {
    public PlsqlElemInfo(PlsqlElemHelper helper) {
        super();
        if (helper.typeName != null && helper.typeSubname != null) {
            elemTypeName = helper.typeName + "." + helper.typeSubname;
        }
        else if (helper.typeSubname != null) {
            elemTypeName = helper.typeSubname;
        }
        else if (helper.typeName != null) {
            elemTypeName = helper.typeName;
        }
        else {
            elemTypeName = helper.dataType;
        }
        if (elemTypeName != null && elemTypeName.equals("PL/SQL BOOLEAN")) {
            elemTypeName = "BOOLEAN";
        }
        elemTypeOwner = helper.typeOwner;
        elemTypeSequence = helper.sequence;
        elemTypePackageName = helper.packageName;
        elemTypeMethodName = helper.objectName;
        elemTypeMethodNo = helper.overload;
        elemTypeLength = helper.dataLength;
        elemTypePrecision = helper.dataPrecision;
        elemTypeScale = helper.dataScale;
        elemTypeMod = "";
        if ("REF".equals(helper.dataType) || "PL/SQL RECORD".equals(helper.dataType)
            || "PL/SQL TABLE".equals(helper.dataType) || "TABLE".equals(helper.dataType)
            || "VARRAY".equals(helper.dataType)) {
            elemTypeMod = helper.dataType;
        }
    }

    public String toString() {
        return super.toString() + "," + elemTypeSequence + "," + elemTypePackageName + ","
            + elemTypeMod + "," + elemTypeMethodName + "," + elemTypeMethodNo;
    }

    public int elemTypeSequence;
    public String elemTypePackageName;
    public String elemTypeMethodName;
    public String elemTypeMethodNo;
}

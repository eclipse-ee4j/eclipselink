/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

public class PlsqlElemInfo extends ElemInfo {

    public int elemTypeSequence;
    public String elemTypePackageName;
    public String elemTypeMethodName;
    public String elemTypeMethodNo;

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
}

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
import java.util.List;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflector;

public class FieldInfo {

    public String fieldName;
    public String fieldTypeName;
    public String fieldTypeSubname;
    public String fieldTypeOwner;
    public String fieldTypeMod;
    public String fieldMethodName;
    public String fieldMethodNo;
    public String fieldPackageName;
    public String fieldCharacterSetName;
    public int fieldSequence;
    public int fieldDataLevel;
    public int fieldDataLength;
    public int fieldDataPrecision;
    public int fieldDataScale;
    public int fieldNo;

    public FieldInfo(AllTypeAttrs r) throws SQLException {
        fieldName = r.attrName;
        fieldTypeName = r.attrTypeName;
        fieldTypeSubname = null;
        fieldTypeOwner = r.attrTypeOwner;
        fieldTypeMod = r.attrTypeMod;
        fieldMethodName = null;
        fieldMethodNo = null;
        fieldPackageName = null;
        fieldSequence = -1;
        fieldDataLevel = -1;
        fieldDataLength = r.attrLength;
        fieldDataPrecision = r.attrPrecision;
        fieldDataScale = r.attrScale;
        fieldNo = r.attrNo;
        fieldCharacterSetName = r.characterSetName;
    }

    public FieldInfo(UserArguments r) throws SQLException {
        fieldNo = r.POSITION;
        fieldName = r.ARGUMENT_NAME;
        fieldTypeOwner = r.TYPE_OWNER;
        fieldTypeName = r.TYPE_NAME;
        if (SqlReflector.isNull(fieldTypeName)) {
            fieldTypeName = r.DATA_TYPE;
        }
        if ("PL/SQL BOOLEAN".equalsIgnoreCase(fieldTypeName)) {
            fieldTypeName = "BOOLEAN";
        }
        fieldTypeSubname = r.TYPE_SUBNAME;
        fieldTypeMod = "";
        if ("REF".equalsIgnoreCase(r.DATA_TYPE) || "PL/SQL RECORD".equalsIgnoreCase(r.DATA_TYPE)
            || "PL/SQL TABLE".equalsIgnoreCase(r.DATA_TYPE)
            || "VARRAY".equalsIgnoreCase(r.DATA_TYPE) || "TABLE".equalsIgnoreCase(r.DATA_TYPE)) {
            fieldTypeMod = r.DATA_TYPE;
        }
        fieldPackageName = r.PACKAGE_NAME;
        fieldMethodName = r.OBJECT_NAME;
        fieldMethodNo = r.OVERLOAD;
        fieldSequence = r.sequence;
        fieldDataLevel = -1;
        if ("PL/SQL RECORD".equalsIgnoreCase(r.DATA_TYPE)) {
            fieldDataLength = 0;
        }
        else {
            fieldDataLength = r.DATA_LENGTH;
        }
        fieldDataPrecision = r.DATA_PRECISION;
        fieldDataScale = r.DATA_SCALE;
        fieldCharacterSetName = r.CHARACTER_SET_NAME;
    }

    public static List<FieldInfo> getFieldInfo(Iterator<ViewRow> iter) throws SQLException {
        ArrayList<ViewRow> v = new ArrayList<ViewRow>();
        while (iter.hasNext()) {
            v.add(iter.next());
        }
        return getFieldInfo(v);
    }

    public static List<FieldInfo> getFieldInfo(ArrayList<ViewRow> v) throws SQLException {
        ArrayList<FieldInfo> a = new ArrayList<FieldInfo>();
        for (int i = 0; i < v.size(); i++) {
            ViewRow vr = v.get(i);
            if (vr.isUserArguments() || vr.isAllArguments()) {
                UserArguments userArguments = (UserArguments)vr;
                a.add(new FieldInfo(userArguments));
            }
            else if (vr.isAllTypeAttrs()) {
                AllTypeAttrs allTypeAttrs = (AllTypeAttrs)vr;
                a.add(new FieldInfo(allTypeAttrs));
            }
        }
        return a;
    }
}
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

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlReflector;

@SuppressWarnings("unchecked")
public class FieldInfo {
    public FieldInfo(AllTypeAttrs r) throws java.sql.SQLException {
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

    public FieldInfo(UserArguments r) throws java.sql.SQLException {
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

    public static FieldInfo[] getFieldInfo(Iterator iter) throws java.sql.SQLException {
        ArrayList v = new ArrayList();
        while (iter.hasNext()) {
            v.add(iter.next());
        }
        return getFieldInfo(v);
    }

    public static FieldInfo[] getFieldInfo(ArrayList v) throws java.sql.SQLException {
        ArrayList a = new ArrayList();
        for (int i = 0; i < v.size(); i++) {
            Object obj = v.get(i);
            if (obj instanceof UserArguments) {
                a.add(new FieldInfo((UserArguments)obj));
            }
            else {
                a.add(new FieldInfo((AllTypeAttrs)obj));
            }
        }
        FieldInfo[] r = new FieldInfo[a.size()];
        for (int i = 0; i < a.size(); i++) {
            r[i] = (FieldInfo)a.get(i);
        }
        return r;
    }

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
}

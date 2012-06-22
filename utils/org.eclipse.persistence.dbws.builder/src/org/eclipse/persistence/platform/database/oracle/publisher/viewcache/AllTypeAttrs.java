/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllTypeAttrs extends ViewRowFactory implements ViewRow {

    public static int iATTR_NAME;
    public static int iATTR_NO;
    public static int iATTR_TYPE_MOD;
    public static int iATTR_TYPE_OWNER;
    public static int iATTR_TYPE_NAME;
    public static int iCHARACTER_SET_NAME;
    public static int iLENGTH;
    public static int iPRECISION;
    public static int iSCALE;
    private static boolean m_indexed = false;

    // Attributes
    public String attrName;
    public int attrNo;
    public String attrTypeMod;
    public String attrTypeOwner;
    public String attrTypeName;
    public String characterSetName;
    public int attrLength;
    public int attrPrecision;
    public int attrScale;

    public AllTypeAttrs(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iATTR_NAME = rs.findColumn("ATTR_NAME");
            iATTR_NO = rs.findColumn("ATTR_NO");
            iATTR_TYPE_MOD = rs.findColumn("ATTR_TYPE_MOD");
            iATTR_TYPE_OWNER = rs.findColumn("ATTR_TYPE_OWNER");
            iATTR_TYPE_NAME = rs.findColumn("ATTR_TYPE_NAME");
            iLENGTH = rs.findColumn("LENGTH");
            iPRECISION = rs.findColumn("PRECISION");
            iSCALE = rs.findColumn("SCALE");
            iCHARACTER_SET_NAME = rs.findColumn("CHARACTER_SET_NAME");
        }
        attrName = rs.getString(iATTR_NAME);
        attrNo = rs.getInt(iATTR_NO);
        attrTypeMod = rs.getString(iATTR_TYPE_MOD);
        attrTypeOwner = rs.getString(iATTR_TYPE_OWNER);
        attrTypeName = rs.getString(iATTR_TYPE_NAME);
        attrLength = rs.getInt(iLENGTH);
        attrPrecision = rs.getInt(iPRECISION);
        attrScale = rs.getInt(iSCALE);
        characterSetName = rs.getString(iCHARACTER_SET_NAME);
    }

    @Override
    public boolean isAllTypeAttrs() {
        return true;
    }

    public String toString() {
        return attrName + "," + attrNo + "," + attrTypeMod + "," + attrTypeOwner + ","
            + attrTypeName + "," + attrLength + "," + attrPrecision + "," + attrScale + ","
            + characterSetName;
    }

    public static String[] getProjectList() {
        return new String[]{"ATTR_NAME", "ATTR_NO", "ATTR_TYPE_MOD", "ATTR_TYPE_OWNER",
            "ATTR_TYPE_NAME", "LENGTH", "SCALE", "CHARACTER_SET_NAME"};
    }
}

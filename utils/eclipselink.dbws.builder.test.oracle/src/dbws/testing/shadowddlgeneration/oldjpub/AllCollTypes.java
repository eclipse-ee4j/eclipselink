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
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllCollTypes extends ViewRowFactory implements ViewRow {

    public static int iCOLL_TYPE;
    public static int iELEM_TYPE_NAME;
    public static int iELEM_TYPE_OWNER;
    public static int iELEM_TYPE_MOD;
    public static int iLENGTH;
    public static int iPRECISION;
    public static int iSCALE;
    public static int iCHARACTER_SET_NAME;
    private static boolean m_indexed = false;

    // Attributes
    public String collType;
    public String elemTypeName;
    public String elemTypeOwner;
    public String elemTypeMod;
    public int elemLength;
    public int elemPrecision;
    public int elemScale;
    public String characterSetName;

    public AllCollTypes(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iCOLL_TYPE = rs.findColumn("COLL_TYPE");
            iELEM_TYPE_NAME = rs.findColumn("ELEM_TYPE_NAME");
            iELEM_TYPE_OWNER = rs.findColumn("ELEM_TYPE_OWNER");
            iELEM_TYPE_MOD = rs.findColumn("ELEM_TYPE_MOD");
            iLENGTH = rs.findColumn("LENGTH");
            iPRECISION = rs.findColumn("PRECISION");
            iSCALE = rs.findColumn("SCALE");
            iCHARACTER_SET_NAME = rs.findColumn("CHARACTER_SET_NAME");
        }
        collType = rs.getString(iCOLL_TYPE);
        elemTypeName = rs.getString(iELEM_TYPE_NAME);
        elemTypeOwner = rs.getString(iELEM_TYPE_OWNER);
        elemTypeMod = rs.getString(iELEM_TYPE_MOD);
        elemLength = rs.getInt(iLENGTH);
        elemPrecision = rs.getInt(iPRECISION);
        elemScale = rs.getInt(iSCALE);
        characterSetName = rs.getString(iCHARACTER_SET_NAME);
    }

    @Override
    public boolean isAllCollTypes() {
        return true;
    }

    public String toString() {
        return collType + "," + elemTypeName + "," + elemTypeOwner + "," + elemTypeMod + ","
            + characterSetName + "," + elemLength + "," + elemPrecision + "," + elemScale;
    }

    public static String[] getProjectList() {
        return new String[]{"COLL_TYPE", "ELEM_TYPE_NAME", "ELEM_TYPE_OWNER", "ELEM_TYPE_MOD",
            "CHARACTER_SET_NAME", "LENGTH", "PRECISION", "SCALE"};
    }
}

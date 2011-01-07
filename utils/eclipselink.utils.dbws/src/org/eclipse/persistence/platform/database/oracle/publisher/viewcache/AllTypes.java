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
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllTypes extends ViewRowFactory implements ViewRow {

    public static int iTYPE_NAME;
    public static int iOWNER;
    public static int iTYPECODE;
    public static int iTYPE_OID;
    public static int iSUPERTYPE_NAME;
    public static int iSUPERTYPE_OWNER;
    public static int iFINAL;
    public static int iINSTANTIABLE;
    public static int iINCOMPLETE;
    public static int iPREDEFINED;
    private static boolean m_indexed = false;

    // Attributes
    public String typeName;
    public String owner;
    public String typeCode;
    public byte[] typeOid;
    public String supertypeName;
    public String supertypeOwner;
    public String finalProp;
    public String instantiable;
    public String incomplete;
    public String predefined;

    public AllTypes(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iTYPE_NAME = rs.findColumn("TYPE_NAME");
            iOWNER = rs.findColumn("OWNER");
            iTYPECODE = rs.findColumn("TYPECODE");
            iTYPE_OID = rs.findColumn("TYPE_OID");
            iSUPERTYPE_NAME = rs.findColumn("SUPERTYPE_NAME");
            iSUPERTYPE_OWNER = rs.findColumn("SUPERTYPE_OWNER");
            iFINAL = rs.findColumn("FINAL");
            iINSTANTIABLE = rs.findColumn("INSTANTIABLE");
            iINCOMPLETE = rs.findColumn("INCOMPLETE");
            iPREDEFINED = rs.findColumn("PREDEFINED");
        }
        typeName = rs.getString(iTYPE_NAME);
        owner = rs.getString(iOWNER);
        typeCode = rs.getString(iTYPECODE);
        typeOid = rs.getBytes(iTYPE_OID);
        supertypeName = rs.getString(iSUPERTYPE_NAME);
        supertypeOwner = rs.getString(iSUPERTYPE_OWNER);
        try {
            finalProp = rs.getString(iFINAL);
            instantiable = rs.getString(iINSTANTIABLE);
            incomplete = rs.getString(iINCOMPLETE);
        }
        catch (java.sql.SQLException se) {
            finalProp = "YES";
            instantiable = "YES";
            incomplete = "YES";
        }
        predefined = rs.getString(iPREDEFINED);
    }

    @Override
    public boolean isAllTypes() {
        return true;
    }

    public String toString() {
        return owner + "," + typeName + "," + typeCode + "," + new String(typeOid) + ","
            + supertypeName + "," + supertypeOwner + "," + finalProp + "," + instantiable + ","
            + incomplete + "," + predefined;
    }

    public static String[] getProjectList() {
        return new String[]{"OWNER", "TYPE_NAME", "TYPECODE", "TYPE_OID", "SUPERTYPE_NAME",
            "SUPERTYPE_OWENER", "FINAL", "INSTANTIABLE", "INCOMPLETE", "PREDEFINED"};
    }
}

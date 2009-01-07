package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

public class AllTypes extends ViewRowFactory implements ViewRow {
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

    public AllTypes(ResultSet rs) throws java.sql.SQLException {
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

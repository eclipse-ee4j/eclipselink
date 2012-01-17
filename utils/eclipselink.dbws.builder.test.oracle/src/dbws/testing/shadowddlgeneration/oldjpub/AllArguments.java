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
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllArguments extends UserArguments implements ViewRow {

    private static int iOWNER = -1;

    public String owner;

    public AllArguments(ResultSet rs) throws SQLException {
        super();
        if (iOWNER == -1) {
            iOWNER = rs.findColumn("OWNER");
            iPACKAGE_NAME = rs.findColumn("PACKAGE_NAME");
            iOBJECT_NAME = rs.findColumn("OBJECT_NAME");
            iOVERLOAD = rs.findColumn("OVERLOAD");
            iARGUMENT_NAME = rs.findColumn("ARGUMENT_NAME");
            iPOSITION = rs.findColumn("POSITION");
            iSEQUENCE = rs.findColumn("SEQUENCE");
            iDATA_TYPE = rs.findColumn("DATA_TYPE");
            iIN_OUT = rs.findColumn("IN_OUT");
            iDATA_LENGTH = rs.findColumn("DATA_LENGTH");
            iDATA_PRECISION = rs.findColumn("DATA_PRECISION");
            iDATA_SCALE = rs.findColumn("DATA_SCALE");
            iCHARACTER_SET_NAME = rs.findColumn("CHARACTER_SET_NAME");
            iTYPE_OWNER = rs.findColumn("TYPE_OWNER");
            iTYPE_NAME = rs.findColumn("TYPE_NAME");
            iTYPE_SUBNAME = rs.findColumn("TYPE_SUBNAME");
            iPLS_TYPE = rs.findColumn("PLS_TYPE");
            iDATA_LEVEL = rs.findColumn("DATA_LEVEL");
            iOBJECT_ID = rs.findColumn("OBJECT_ID");
        }
        PACKAGE_NAME = rs.getString(iPACKAGE_NAME);
        OBJECT_NAME = rs.getString(iOBJECT_NAME);
        OVERLOAD = rs.getString(iOVERLOAD);
        ARGUMENT_NAME = rs.getString(iARGUMENT_NAME);
        POSITION = rs.getInt(iPOSITION);
        sequence = rs.getInt(iSEQUENCE);
        DATA_TYPE = rs.getString(iDATA_TYPE);
        IN_OUT = rs.getString(iIN_OUT);
        DATA_LENGTH = rs.getInt(iDATA_LENGTH);
        DATA_PRECISION = rs.getInt(iDATA_PRECISION);
        DATA_SCALE = rs.getInt(iDATA_SCALE);
        CHARACTER_SET_NAME = rs.getString(iCHARACTER_SET_NAME);
        TYPE_OWNER = rs.getString(iTYPE_OWNER);
        TYPE_NAME = rs.getString(iTYPE_NAME);
        TYPE_SUBNAME = rs.getString(iTYPE_SUBNAME);
        PLS_TYPE = rs.getString(iPLS_TYPE);
        DATA_LEVEL = rs.getInt(iDATA_LEVEL);
        OBJECT_ID = rs.getInt(iOBJECT_ID);
        owner = rs.getString(iOWNER);
    }

    @Override
    public boolean isUserArguments() {
        return false;
    }

    @Override
    public boolean isAllArguments() {
        return true;
    }

    public String toString() {
        return owner + OBJECT_NAME + "," + PACKAGE_NAME + "," + OVERLOAD + "," + ARGUMENT_NAME
            + "," + POSITION + "," + sequence + "," + DATA_LEVEL + "," + DATA_TYPE + "," + IN_OUT
            + "," + DATA_LENGTH + "," + DATA_PRECISION + "," + DATA_SCALE + ","
            + CHARACTER_SET_NAME + "," + TYPE_OWNER + "," + TYPE_NAME + "," + TYPE_SUBNAME + ","
            + OBJECT_ID;
    }

    public static String[] getProjectList() {
        return new String[]{"PACKAGE_NAME", "TYPE_NAME", "TYPE_SUBNAME", "OBJECT_NAME", "OVERLOAD",
            "ARGUMENT_NAME", "IN_OUT", "DATA_TYPE", "PLS_TYPE", "DATA_LEVEL", "SEQUENCE",
            "POSITION", "TYPE_OWNER", "DATA_LENGTH", "DATA_PRECISION", "DATA_SCALE",
            "CHARACTER_SET_NAME", "OBJECT_ID", "OWNER",};
    }
}

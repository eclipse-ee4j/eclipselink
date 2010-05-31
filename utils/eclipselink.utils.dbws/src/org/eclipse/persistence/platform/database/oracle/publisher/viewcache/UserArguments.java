/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.util.ArrayList;

// Includes all the columns in ALL_ARGUMENTS and USER_AUGUMENTS

public class UserArguments extends ViewRowFactory implements ViewRow {
    // Attributes
    public String PACKAGE_NAME;
    public String OBJECT_NAME; // DO _NOT_ REFACTOR THIS!
    public String OVERLOAD; // DO _NOT_ REFACTOR THIS!
    public String ARGUMENT_NAME; // DO _NOT_ REFACTOR THIS!
    public int POSITION; // DO _NOT_ REFACTOR THIS!
    public int sequence;
    public int DATA_LEVEL; // DO _NOT_ REFACTOR THIS!
    public String DATA_TYPE;
    public String IN_OUT;
    public int DATA_LENGTH;
    public int DATA_PRECISION;
    public int DATA_SCALE;
    public String CHARACTER_SET_NAME;
    public String TYPE_OWNER;
    public String TYPE_NAME;
    public String TYPE_SUBNAME;
    public String PLS_TYPE;
    public int OBJECT_ID;

    public static int iPACKAGE_NAME;
    public static int iOBJECT_NAME;
    public static int iOVERLOAD;
    public static int iARGUMENT_NAME;
    public static int iPOSITION;
    public static int iSEQUENCE;
    public static int iDATA_LEVEL;
    public static int iDATA_TYPE;
    public static int iIN_OUT;
    public static int iDATA_LENGTH;
    public static int iDATA_PRECISION;
    public static int iDATA_SCALE;
    public static int iCHARACTER_SET_NAME;
    public static int iTYPE_OWNER;
    public static int iTYPE_NAME;
    public static int iTYPE_SUBNAME;
    public static int iPLS_TYPE;
    public static int iOBJECT_ID;
    private static boolean m_indexed = false;

    protected UserArguments() {
        super();
    }

    public UserArguments(ResultSet rs) throws java.sql.SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
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
    }

    @Override
    public boolean isUserArguments() {
        return true;
    }

    public String toString() {
        return OBJECT_NAME + "," + PACKAGE_NAME + "," + OVERLOAD + "," + ARGUMENT_NAME + ","
            + POSITION + "," + sequence + "," + DATA_LEVEL + "," + DATA_TYPE + "," + IN_OUT + ","
            + DATA_LENGTH + "," + DATA_PRECISION + "," + DATA_SCALE + "," + CHARACTER_SET_NAME
            + "," + TYPE_OWNER + "," + TYPE_NAME + "," + TYPE_SUBNAME + "," + OBJECT_ID;
    }

    public static void orderByPosition(ArrayList<ViewRow> rows) {
        for (int i = 0; i < rows.size() - 1; i++) {
            UserArguments rowi = (UserArguments)rows.get(i);
            int minIdx = i;
            int minPos = rowi.POSITION;
            for (int j = i; j < rows.size(); j++) {
                UserArguments rowj = (UserArguments)rows.get(j);
                if (minPos > rowj.POSITION) {
                    minIdx = j;
                    minPos = rowj.POSITION;
                }
            }
            if (i != minIdx) {
                ViewRow tmp = rows.get(i);
                rows.set(i, rows.get(minIdx));
                rows.set(minIdx, tmp);
            }
        }
    }

    public static void orderBySequence(ArrayList<ViewRow> rows) {
        for (int i = 0; i < rows.size() - 1; i++) {
            UserArguments rowi = (UserArguments)rows.get(i);
            int minIdx = i;
            int minPos = rowi.sequence;
            for (int j = i; j < rows.size(); j++) {
                UserArguments rowj = (UserArguments)rows.get(j);
                if (minPos > rowj.sequence) {
                    minIdx = j;
                    minPos = rowj.sequence;
                }
            }
            if (i != minIdx) {
                ViewRow tmp = rows.get(i);
                rows.set(i, rows.get(minIdx));
                rows.set(minIdx, tmp);
            }
        }
    }

    public static String[] getProjectList() {
        return new String[]{"PACKAGE_NAME", "TYPE_NAME", "TYPE_SUBNAME", "OBJECT_NAME", "OVERLOAD",
            "ARGUMENT_NAME", "IN_OUT", "DATA_TYPE", "PLS_TYPE", "DATA_LEVEL", "SEQUENCE",
            "POSITION", "TYPE_OWNER", "DATA_LENGTH", "DATA_PRECISION", "DATA_SCALE",
            "CHARACTER_SET_NAME", "OBJECT_ID"};
    }
}
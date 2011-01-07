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
import java.util.List;

public class RowtypeInfo extends SequencedInfo {

    public String argument_name;
    public String type_owner;
    public String type_name;
    public String type_subname;
    public String modifier;
    public String data_type;
    public int data_level;
    public int data_length;
    public int data_precision;
    public int data_scale;

    public RowtypeInfo(UserArguments item) {
        argument_name = item.ARGUMENT_NAME;
        type_owner = item.TYPE_OWNER;
        /*
         * if (item.TYPE_NAME!=null && item.TYPE_SUBNAME!=null) { type_name =
         * item.TYPE_NAME+"."+item.TYPE_SUBNAME; } else if (item.TYPE_SUBNAME!=null) { type_name =
         * item.TYPE_SUBNAME; } else if (item.TYPE_NAME!=null) { type_name = item.TYPE_NAME; } else
         * { type_name = item.DATA_TYPE; }
         */
        if (item.TYPE_NAME == null && item.TYPE_SUBNAME == null) {
            type_name = item.DATA_TYPE;
        }
        else {
            type_name = item.TYPE_NAME;
        }
        if (type_name != null && type_name.equals("PL/SQL BOOLEAN")) {
            type_name = "BOOLEAN";
        }
        type_subname = item.TYPE_SUBNAME;
        sequence = item.sequence;
        data_type = item.DATA_TYPE;
        data_level = item.DATA_LEVEL;
        data_length = item.DATA_LENGTH;
        data_precision = item.DATA_PRECISION;
        data_scale = item.DATA_SCALE;
        modifier = "";
        if ("REF".equals(item.DATA_TYPE) || "PL/SQL RECORD".equals(item.DATA_TYPE)
            || "PL/SQL TABLE".equals(item.DATA_TYPE) || "TABLE".equals(item.DATA_TYPE)
            || "VARRAY".equals(item.DATA_TYPE)) {
            modifier = item.DATA_TYPE;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof RowtypeInfo)) {
            return false;
        }
        RowtypeInfo p = (RowtypeInfo)o;
        if (argument_name == null && p.argument_name != null) {
            return false;
        }
        if (type_owner == null && p.type_owner != null) {
            return false;
        }
        if (type_subname == null && p.type_subname != null) {
            return false;
        }
        if (type_subname != null && p.type_subname == null) {
            return false;
        }
        if (type_subname != null && p.type_subname != null && !type_subname.equals(p.type_subname)) {
            return false;
        }
        if (data_type == null && p.data_type != null) {
            return false;
        }
        if (data_type != null && p.data_type == null) {
            return false;
        }
        if (data_type != null && p.data_type != null && !data_type.equals(p.data_type)) {
            return false;
        }
        if (data_level != p.data_level) {
            return false;
        }
        if (data_length != p.data_length) {
            return false;
        }
        if (data_precision != p.data_precision) {
            return false;
        }
        if (data_scale != p.data_scale) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "RowtypeInfo(" + argument_name + "," + type_owner + "," + type_name + ","
            + type_subname + "," + modifier + "," + data_type + "," + data_level + ","
            + data_length + "," + data_precision + "," + data_scale + "," + sequence() + ")";
    }

    public int data_level() {
        return data_level;
    }

    public void data_level(int dl) {
        data_level = dl;
    }

    public static List<RowtypeInfo> getRowtypeInfo(ArrayList<ViewRow> viewRows) throws SQLException {
        ArrayList<RowtypeInfo> a = new ArrayList<RowtypeInfo>();
        for (int i = 0; i < viewRows.size(); i++) {
            RowtypeInfo rif = new RowtypeInfo((UserArguments)viewRows.get(i));
            a.add(rif);
        }
        if (a.size() == 0) {
            return null;
        }
        RowtypeInfo[] r = a.toArray(new RowtypeInfo[a.size()]);
        RowtypeInfo[] rr = (RowtypeInfo[])reorder(r);
        ArrayList<RowtypeInfo> aa = new ArrayList<RowtypeInfo>();
        for (RowtypeInfo rti : rr) {
            aa.add(rti);
        }
        return aa;
    }
}
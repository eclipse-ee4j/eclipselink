/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_ARGUMENTS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.DATA_LEVEL;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OBJECT_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OVERLOAD;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OWNER;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.PACKAGE_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.SEQUENCE;

//javase imports
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlsqlRecordType extends SqlTypeWithFields {

    // Locating PL/SQL RECORD, esp., ROWTYPE in ALL_ARGUMENTS
    protected List<FieldInfo> m_fieldInfo;
    protected List<RowtypeInfo> m_rowtypeInfo;

    public PlsqlRecordType(SqlName sqlName, List<FieldInfo> fieldInfo, List<AttributeField> fields,
        List<RowtypeInfo> rowtypeInfo, boolean generateMe, SqlType parentType, SqlReflector reflector)
        throws SQLException {
        super(sqlName, OracleTypes.PLSQL_RECORD, generateMe, parentType, reflector);
        m_rowtypeInfo = rowtypeInfo;
        m_fieldInfo = fieldInfo;
        m_fields = fields;
    }

    @Override
    public List<AttributeField> getDeclaredFields(boolean publishedOnly) throws SQLException,
        PublisherException {
        if (!publishedOnly) {
            return m_fields;
        }
        else if (m_fieldsPublishedOnly != null) {
            return m_fieldsPublishedOnly;
        }
        else {
            ArrayList<AttributeField> fieldsCS = new ArrayList<AttributeField>();
            PLSQLMap map = new PLSQLMap(m_parentType, m_reflector);
            for (int i = 0; i < m_fields.size(); i++) {
                if (map.getMemberName(m_fields.get(i).getName()) != null) {
                    fieldsCS.add(m_fields.get(i));
                }
            }
            m_fieldsPublishedOnly = new ArrayList<AttributeField>(fieldsCS);
            return m_fieldsPublishedOnly;
        }
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    @Override
    public List<AttributeField> getFields(boolean publishedOnly) throws SecurityException,
        SQLException, PublisherException {
        return getDeclaredFields(publishedOnly);
    }

    @Override
    protected List<FieldInfo> getFieldInfo() {
        return m_fieldInfo;
    }

    public static List<FieldInfo> getFieldInfo(String schema, String packageName, String methodName, String methodNo,
        int sequence, SqlReflector reflector) throws SQLException {

        int data_level = -1;
        int next_rec_sequence = -1;

        // Although package_name and type_name derived from getPlsqlTypeName()
        // can be used for the queries, we use method and sequence
        // to be more general. If this type is defined via CURSOR%ROWTYPE,
        // for which method, method_no and sequence has to be used to
        // identify this type in ALL_ARGUMENTS.

        ViewCache viewCache = reflector.getViewCache();
        Iterator<ViewRow> iter = viewCache.getRows(ALL_ARGUMENTS, new String[0], new String[]{
            OWNER, PACKAGE_NAME, OBJECT_NAME, OVERLOAD}, new Object[]{schema, packageName,
            methodName, methodNo}, new String[]{SEQUENCE});
        ArrayList<ViewRow> viewRows = new ArrayList<ViewRow>();
        while (iter.hasNext()) {
            UserArguments item = (UserArguments)iter.next();
            viewRows.add(item);
        }
        PlsqlTypeInfo[] info = PlsqlTypeInfo.getPlsqlTypeInfo(viewRows);
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (data_level == -1 && (sequence == -1 || sequence == info[i].sequence)) {
                    data_level = info[i].dataLevel; // Data level for the record
                }
                if (data_level > -1 && data_level == info[i].dataLevel && next_rec_sequence == -1
                    && sequence < info[i].sequence) {
                    next_rec_sequence = info[i].sequence;
                    break;
                }
            }
        }
        data_level++;
        iter = viewCache.getRows(ALL_ARGUMENTS, new String[0], new String[]{OWNER, PACKAGE_NAME,
            OBJECT_NAME, OVERLOAD, DATA_LEVEL}, new Object[]{schema, packageName,
            methodName, methodNo, Integer.valueOf(data_level)}, new String[]{SEQUENCE});
        viewRows = new ArrayList<ViewRow>();
        while (iter.hasNext()) { // DISTINCT
            UserArguments item = (UserArguments)iter.next();
            if ((sequence == -1 || item.sequence > sequence)
                && (next_rec_sequence == -1 || item.sequence < next_rec_sequence)) {
                viewRows.add(item);
            }
        }
        return FieldInfo.getFieldInfo(viewRows);
    }

    public List<RowtypeInfo> getRowtypeInfo() {
        return m_rowtypeInfo;
    }
}

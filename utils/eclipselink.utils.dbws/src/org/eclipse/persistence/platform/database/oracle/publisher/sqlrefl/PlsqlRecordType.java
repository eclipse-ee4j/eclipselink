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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.FieldInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.PlsqlTypeInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.RowtypeInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.UserArguments;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewCache;

@SuppressWarnings("unchecked")
public class PlsqlRecordType extends SqlTypeWithFields {
    public PlsqlRecordType(SqlName sqlName, FieldInfo[] fieldInfo, AttributeField[] fields,
        RowtypeInfo[] rowtypeInfo, boolean generateMe, SqlType parentType, SqlReflector reflector)
        throws SQLException {
        super(sqlName, OracleTypes.PLSQL_RECORD, generateMe, parentType, reflector);
        m_rowtypeInfo = rowtypeInfo;
        m_fieldInfo = fieldInfo;
        m_fields = fields;
    }

    public AttributeField[] getDeclaredFields(boolean publishedOnly) throws java.sql.SQLException,
        PublisherException {
        if (!publishedOnly) {
            return m_fields;
        }
        else if (m_fieldsPublishedOnly != null) {
            return m_fieldsPublishedOnly;
        }
        else {
            ArrayList fieldsCS = new ArrayList();
            PLSQLMap map = new PLSQLMap(m_parentType, m_reflector);
            for (int i = 0; i < m_fields.length; i++) {
                if (map.getMemberName(m_fields[i].getName()) != null) {
                    fieldsCS.add(m_fields[i]);
                }
            }
            m_fieldsPublishedOnly = new AttributeField[fieldsCS.size()];
            for (int i = 0; i < m_fieldsPublishedOnly.length; i++) {
                m_fieldsPublishedOnly[i] = (AttributeField)fieldsCS.get(i);
            }
            return m_fieldsPublishedOnly;
        }
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    public AttributeField[] getFields(boolean publishedOnly) throws SecurityException,
        java.sql.SQLException, PublisherException {
        return getDeclaredFields(publishedOnly);
    }

    protected FieldInfo[] getFieldInfo() {
        return m_fieldInfo;
    }

    static FieldInfo[] getFieldInfo(String packageName, String methodName, String methodNo,
        int sequence, SqlReflector reflector) throws SQLException {

        int data_level = -1;
        int next_rec_sequence = -1;

        // Although package_name and type_name derived from getPlsqlTypeName()
        // can be used for the queries, we use method and sequence
        // to be more general. If this type is defined via CURSOR%ROWTYPE,
        // for which method, method_no and sequence has to be used to
        // identify this type in ALL_ARGUMENTS.

        ViewCache viewCache = reflector.getViewCache();
        Iterator iter = viewCache.getRows(Util.ALL_ARGUMENTS, new String[0], new String[]{
            Util.PACKAGE_NAME, Util.OBJECT_NAME, Util.OVERLOAD}, new Object[]{packageName,
            methodName, methodNo}, new String[]{Util.SEQUENCE});
        ArrayList viewRows = new ArrayList();
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

        iter = viewCache.getRows(Util.ALL_ARGUMENTS, new String[0], new String[]{Util.PACKAGE_NAME,
            Util.OBJECT_NAME, Util.OVERLOAD, Util.DATA_LEVEL}, new Object[]{packageName,
            methodName, methodNo, new Integer(data_level)}, new String[]{Util.SEQUENCE});
        viewRows = new ArrayList();
        while (iter.hasNext()) { // DISTINCT
            UserArguments item = (UserArguments)iter.next();
            if ((sequence == -1 || item.sequence > sequence)
                && (next_rec_sequence == -1 || item.sequence < next_rec_sequence)) {
                viewRows.add(item);
            }
        }
        return FieldInfo.getFieldInfo(viewRows);
    }
    
    RowtypeInfo[] getRowtypeInfo() {
        return m_rowtypeInfo;
    }

    // Locating PL/SQL RECORD, esp., ROWTYPE in ALL_ARGUMENTS
    private FieldInfo[] m_fieldInfo;
    private RowtypeInfo m_rowtypeInfo[];
}

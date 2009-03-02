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
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.FieldInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewCache;

@SuppressWarnings("unchecked")
public abstract class SqlTypeWithFields extends SqlType {

    public SqlTypeWithFields(SqlName sqlName, int typecode, boolean generateMe, SqlType parentType,
        SqlReflector reflector) throws SQLException {
        super(sqlName, typecode, generateMe, parentType, reflector);
    }

    /**
     * Returns an array of Field objects reflecting all the fields declared by this
     * SqlTypeWithFields object. Returns an array of length 0 if this SqlTypeWithFields object
     * declares no fields.
     */
    // [3190197] Add publishedOnly: false - return Fields including
    // those not published as well
    public AttributeField[] getDeclaredFields(boolean publishedOnly) throws java.sql.SQLException,
        PublisherException {
        if (publishedOnly) {
            if (m_fieldsPublishedOnly == null) {
                m_fieldsPublishedOnly = reflectFields(publishedOnly);
            }
            return m_fieldsPublishedOnly;
        }
        else {
            // return unpublished fields also
            if (m_fields == null) {
                m_fields = reflectFields(publishedOnly);
            }
            return m_fields;
        }
    }

    private AttributeField[] reflectFields(boolean publishedOnly) throws SQLException, PublisherException {
        return reflectFields(publishedOnly, getFieldInfo(), m_reflector, this, false);
    }

    static AttributeField[] reflectFields(boolean publishedOnly, FieldInfo[] sfi, SqlReflector reflector,
        SqlType parent, boolean isGrandparent) throws SQLException, PublisherException {
        ArrayList fieldsCS = new ArrayList();
        ViewCache viewCache = reflector.getViewCache();
        // JavaMap map = new JavaMap(parent, reflector);
        Typemap map = new Typemap(parent, reflector);
        for (int ii = 0; sfi != null && ii < sfi.length; ii++) {
            try {
                @SuppressWarnings("unused")
                int idx = sfi[ii].fieldNo;
                if (publishedOnly && map.getMemberName(sfi[ii].fieldName) == null) {
                    continue;
                }
                // [2954993] Workaround
                String fieldTypeOwner = sfi[ii].fieldTypeOwner;
                if (m_builtin.get(sfi[ii].fieldTypeName) == null) {
                    Iterator iter = viewCache.getRows(Util.ALL_TYPES, new String[0], new String[]{
                        "OWNER", Util.TYPE_NAME, "PREDEFINED"}, new Object[]{fieldTypeOwner,
                        sfi[ii].fieldTypeName, "NO"}, new String[0]);
                    if (!iter.hasNext()) {
                        iter = viewCache.getRows(Util.ALL_TYPES, new String[0], new String[]{
                            "TYPE_NAME", "PREDEFINED"}, new Object[]{sfi[ii].fieldTypeName, "NO"},
                            new String[0]);
                        if (iter.hasNext()) {
                            fieldTypeOwner = ((AllTypes)iter.next()).owner;
                        }
                    }
                }
                fieldsCS.add(new AttributeField(sfi[ii].fieldName, reflector.addPlsqlDBType(fieldTypeOwner,
                    sfi[ii].fieldTypeName, sfi[ii].fieldTypeSubname, sfi[ii].fieldTypeMod,
                    false, // No NCHAR-considerations for fields!
                    sfi[ii].fieldPackageName, sfi[ii].fieldMethodName, sfi[ii].fieldMethodNo,
                    sfi[ii].fieldSequence, parent, isGrandparent), sfi[ii].fieldDataLength,
                    sfi[ii].fieldDataPrecision, sfi[ii].fieldDataScale,
                    sfi[ii].fieldCharacterSetName, reflector));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            ;
        }
        AttributeField[] sqlFields = new AttributeField[fieldsCS.size()];
        for (int i = 0; i < sqlFields.length; i++) {
            sqlFields[i] = (AttributeField)fieldsCS.get(i);
        }
        return sqlFields;
    }

    protected abstract FieldInfo[] getFieldInfo() throws SQLException;

    protected AttributeField[] m_fieldsPublishedOnly;
    protected AttributeField[] m_fields;
    protected static HashMap m_builtin;

    static {
        m_builtin = new HashMap();
        m_builtin.put("VARCHAR2", Boolean.TRUE);
        m_builtin.put("NUMBER", Boolean.TRUE);
        m_builtin.put("DATE", Boolean.TRUE);
        m_builtin.put("BOOLEAN", Boolean.TRUE);
    }
}

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
package  dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.AllTypes;
import dbws.testing.shadowddlgeneration.oldjpub.FieldInfo;
import dbws.testing.shadowddlgeneration.oldjpub.ViewCache;
import dbws.testing.shadowddlgeneration.oldjpub.ViewRow;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_TYPES;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.TYPE_NAME;

public abstract class SqlTypeWithFields extends SqlType {

    protected static Map<String, Boolean> m_builtin;
    static {
        m_builtin = new HashMap<String, Boolean>();
        m_builtin.put("VARCHAR2", Boolean.TRUE);
        m_builtin.put("NUMBER", Boolean.TRUE);
        m_builtin.put("DATE", Boolean.TRUE);
        m_builtin.put("BOOLEAN", Boolean.TRUE);
    }

    protected List<AttributeField> m_fieldsPublishedOnly;
    protected List<AttributeField> m_fields;

    public SqlTypeWithFields(SqlName sqlName, int typecode, boolean generateMe, SqlType parentType,
        SqlReflector reflector) throws SQLException {
        super(sqlName, typecode, generateMe, parentType, reflector);
    }

    public boolean isTopLevel() {
        return false;
    }

    /**
     * Returns an array of Field objects reflecting all the fields declared by this
     * SqlTypeWithFields object. Returns an array of length 0 if this SqlTypeWithFields object
     * declares no fields.
     */
    // [3190197] Add publishedOnly: false - return Fields including
    // those not published as well
    public List<AttributeField> getDeclaredFields(boolean publishedOnly) throws SQLException,
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

    private List<AttributeField> reflectFields(boolean publishedOnly) throws SQLException,
        PublisherException {
        return reflectFields(publishedOnly, getFieldInfo(), m_reflector, this, false);
    }

    public static List<AttributeField> reflectFields(boolean publishedOnly, List<FieldInfo> sfi,
        SqlReflector reflector, SqlType parent, boolean isGrandparent) throws SQLException,
        PublisherException {
        ArrayList<AttributeField> fieldsCS = new ArrayList<AttributeField>();
        ViewCache viewCache = reflector.getViewCache();
        // JavaMap map = new JavaMap(parent, reflector);
        Typemap map = new Typemap(parent, reflector);
        for (int ii = 0; sfi != null && ii < sfi.size(); ii++) {
            try {
                FieldInfo fi = sfi.get(ii);
                @SuppressWarnings("unused")
                int idx = fi.fieldNo;
                if (publishedOnly && map.getMemberName(fi.fieldName) == null) {
                    continue;
                }
                // [2954993] Workaround
                String fieldTypeOwner = fi.fieldTypeOwner;
                if (m_builtin.get(fi.fieldTypeName) == null) {
                    Iterator<ViewRow> iter = viewCache.getRows(ALL_TYPES, new String[0],
                        new String[]{"OWNER", TYPE_NAME, "PREDEFINED"}, new Object[]{
                            fieldTypeOwner, fi.fieldTypeName, "NO"}, new String[0]);
                    if (!iter.hasNext()) {
                        iter = viewCache.getRows(ALL_TYPES, new String[0], new String[]{
                            "TYPE_NAME", "PREDEFINED"}, new Object[]{fi.fieldTypeName, "NO"},
                            new String[0]);
                        if (iter.hasNext()) {
                            fieldTypeOwner = ((AllTypes)iter.next()).owner;
                        }
                    }
                }
                fieldsCS.add(new AttributeField(fi.fieldName, reflector.addPlsqlDBType(
                    fieldTypeOwner, fi.fieldTypeName, fi.fieldTypeSubname,
                    fi.fieldTypeMod,
                    false, // No NCHAR-considerations for fields!
                    fi.fieldPackageName, fi.fieldMethodName, fi.fieldMethodNo,
                    fi.fieldSequence, parent, isGrandparent), fi.fieldDataLength,
                    fi.fieldDataPrecision, fi.fieldDataScale,
                    fi.fieldCharacterSetName, reflector));
            }
            catch (SQLException e) {
                e.printStackTrace();
            };
        }
        return fieldsCS;
    }

    protected abstract List<FieldInfo> getFieldInfo() throws SQLException;

}

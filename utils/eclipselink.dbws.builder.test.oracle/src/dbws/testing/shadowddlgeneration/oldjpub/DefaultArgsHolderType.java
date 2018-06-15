/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.FieldInfo;

public class DefaultArgsHolderType extends SqlTypeWithFields {

    protected SqlType m_valueType;

    public DefaultArgsHolderType(SqlName sqlName, SqlType valueType, boolean ncharFormOfUse,
        SqlReflector reflector) throws SQLException {
        super(sqlName, Types.STRUCT, true/* generateMe */, null/* parent */, reflector);
        m_valueType = valueType;
        AttributeField field = new AttributeField("value", m_valueType, 0/* dataLength */,
            0/* precision */, 0/* scale */, ncharFormOfUse, reflector);
        m_fields = new ArrayList<AttributeField>();
        m_fields.add(field);
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    public List<AttributeField> getDeclaredFields(boolean publishedOnly) {
        return m_fields;
    }

    public List<AttributeField> getFields(boolean publishedOnly) throws SecurityException,
        SQLException, PublisherException {
        return m_fields;
    }

    protected List<FieldInfo> getFieldInfo() throws SQLException {
        throw new SQLException("DefaultArgsHolderType#getFieldInfo not supported");
    }

    public SqlType getValueType() {
        return m_valueType;
    }

}

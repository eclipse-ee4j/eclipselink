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

import org.eclipse.persistence.platform.database.oracle.publisher.Util;

/**
 * Description of an attribute for unparsing.
 */

public class AttributeField implements Sortable {
    public AttributeField(String name, TypeClass type, int dataLength, int precision, int scale,
        boolean ncharFormOfUse, SqlReflector reflector) {
        m_name = name;
        m_type = type;
        m_dataLength = dataLength;
        m_precision = precision;
        m_scale = scale;
        m_reflector = reflector;
        m_isNChar = ncharFormOfUse;
    }

    public AttributeField(String name, TypeClass type, int dataLength, int precision, int scale,
        String character_set_name, SqlReflector reflector) {
        this(name, type, dataLength, precision, scale, SqlReflector.NCHAR_CS
            .equals(character_set_name), reflector);
    }

    /**
     * @return the name of the field represented by this Field object
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the Type object of the declared type of the field.
     */
    public TypeClass getType() {
        return m_type;
    }

    /**
     * Returns the Type object of the declared type of the field.
     */
    public int getDataLength() {
        return m_dataLength;
    }

    public int getPrecision() {
        return m_precision;
    }

    public int getScale() {
        return m_scale;
    }

    public boolean isNChar() {
        return m_isNChar;
    }

    public String printTypeWithLength() {
        String typeName = m_type.getName();
        if (m_type instanceof SqlType) {
            typeName = ((SqlType)m_type).getTargetTypeName();
        }
        return Util.printTypeWithLength(typeName, m_dataLength, m_precision, m_scale);
    }

    public String printTypeWithLength(int schemaName) {
        String typeName = m_type.getName();
        if (m_type instanceof SqlType) {
            if (((SqlType)m_type).getSqlName() != null) {
                typeName = ((SqlType)m_type).getSqlName().getFullTargetTypeName(schemaName);
            }
        }
        return Util.printTypeWithLength(typeName, m_dataLength, m_precision, m_scale);
    }

    public String toString() {
        return m_type.toString() + " " + m_name;
    }

    public String getSortingKey() {
        return getName();
    }

    private String m_name;
    private TypeClass m_type;
    private int m_dataLength;
    private int m_precision;
    private int m_scale;
    private boolean m_isNChar;
    @SuppressWarnings("unused")
    private SqlReflector m_reflector;

}

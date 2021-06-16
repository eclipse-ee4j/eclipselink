/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.Util;

/**
 * Description of an attribute for unparsing.
 */

public class AttributeField implements Comparable<AttributeField> {

    protected String m_name;
    protected TypeClass m_type;
    protected int m_dataLength;
    protected int m_precision;
    protected int m_scale;
    protected boolean m_isNChar;
    protected SqlReflector m_reflector;

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

    public int compareTo(AttributeField o) {
        return m_name.compareTo(o.getName());
    }

}

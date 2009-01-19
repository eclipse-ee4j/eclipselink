package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import org.eclipse.persistence.platform.database.oracle.publisher.Util;

/**
 * Description of an attribute for unparsing.
 */

public class Field implements Sortable {
    public Field(String name, Type type, int dataLength, int precision, int scale,
        boolean ncharFormOfUse, SqlReflector reflector) {
        m_name = name;
        m_type = type;
        m_dataLength = dataLength;
        m_precision = precision;
        m_scale = scale;
        m_reflector = reflector;
        m_isNChar = ncharFormOfUse;
    }

    public Field(String name, Type type, int dataLength, int precision, int scale,
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
    public Type getType() {
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
    private Type m_type;
    private int m_dataLength;
    private int m_precision;
    private int m_scale;
    private boolean m_isNChar;
    @SuppressWarnings("unused")
    private SqlReflector m_reflector;

}

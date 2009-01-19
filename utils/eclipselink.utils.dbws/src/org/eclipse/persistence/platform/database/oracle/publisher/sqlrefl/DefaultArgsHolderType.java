package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import java.sql.Types;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.FieldInfo;

public class DefaultArgsHolderType extends SqlTypeWithFields {
    public DefaultArgsHolderType(SqlName sqlName, SqlType valueType, boolean ncharFormOfUse,
        SqlReflector reflector) throws SQLException {
        super(sqlName, Types.STRUCT, true/* generateMe */, null/* parent */, reflector);
        m_valueType = valueType;
        Field field = new Field("value", m_valueType, 0/* dataLength */, 0/* precision */,
            0/* scale */, ncharFormOfUse, reflector);
        m_fields = new Field[]{field};
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    public Field[] getDeclaredFields(boolean publishedOnly) {
        return m_fields;
    }

    public Field[] getFields(boolean publishedOnly) throws SecurityException, SQLException,
        PublisherException {
        return m_fields;
    }

    protected FieldInfo[] getFieldInfo() throws SQLException {
        throw new SQLException("DefaultArgsHolderType#getFieldInfo not supported");
    }

    public SqlType getValueType() {
        return m_valueType;
    }

    private SqlType m_valueType;
}

package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import java.util.Iterator;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllCollTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ElemInfo;

@SuppressWarnings("unchecked")
public class SqlCollectionType extends SqlType {
    public SqlCollectionType(SqlName sqlName, int typecode, boolean generateMe, SqlType parentType,
        SqlReflectorImpl reflector) {
        super(sqlName, typecode, generateMe, parentType, reflector);
        ((JavaName)sqlName.getLangName()).ungenPattern(sqlName.getSimpleName());
    }

    // No database reflection. Currently used by oracle.j2ee.ws.db.genproxy.PlsqlProxy.
    public SqlCollectionType(SqlName sqlName, Type eleType, SqlReflectorImpl reflector)
        throws SQLException {
        super(sqlName, OracleTypes.TABLE, true, null, reflector);
        ((JavaName)sqlName.getLangName()).ungenPattern(sqlName.getSimpleName());
        m_elementType = eleType;
    }

    /**
     * Return the Type object that represents the component type of this collection type.
     */
    public Type getComponentType() throws SQLException, PublisherException {
        if (m_elementType == null) {
            SqlType result = null;
            ElemInfo scti;
            try {
                scti = getElemInfo();
                if (scti != null) {
                    String elemTypeName = scti.elemTypeName;
                    String elemSchemaName = scti.elemTypeOwner;
                    String elemTypeMod = scti.elemTypeMod;
                    m_elemTypeLength = scti.elemTypeLength;
                    m_elemTypePrecision = scti.elemTypePrecision;
                    m_elemTypeScale = scti.elemTypeScale;
                    result = m_reflector.addSqlDBType(elemSchemaName, elemTypeName, null,
                        elemTypeMod, false, this);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                if (result == null) {
                    result = SqlReflectorImpl.UNKNOWN_TYPE;
                }
            }
            m_elementType = result;
        }
        return m_elementType;
    }

    @SuppressWarnings("unused")
    protected ElemInfo getElemInfo() throws SQLException {
        ElemInfo elemInfo = null;
        SqlName sqlName = getSqlName();
        String schema = sqlName.getSchemaName();
        String type = sqlName.getTypeName();

        Iterator scti = m_viewCache.getRows("ALL_COLL_TYPES", new String[0], new String[]{"OWNER",
            "TYPE_NAME"}, new Object[]{schema, type}, new String[0]);

        String elemTypeName = null;
        String elemTypeOwner = null;
        String elemTypeMod = null;
        if (scti.hasNext()) {
            AllCollTypes viewRow = (AllCollTypes)scti.next();
            m_isNChar = SqlReflectorImpl.NCHAR_CS.equals(viewRow.characterSetName);
            elemInfo = new ElemInfo(viewRow);
            if (SqlReflectorImpl.isNull(elemInfo.elemTypeMod)
                && !SqlReflectorImpl.isNull(elemInfo.elemTypeName)) {
                scti = m_viewCache.getRows(Util.ALL_TYPES, new String[0], new String[]{"OWNER",
                    "TYPE_NAME"}, new Object[]{elemInfo.elemTypeOwner, elemInfo.elemTypeName},
                    new String[0]);
                if (scti.hasNext()) {
                    elemInfo.elemTypeMod = ((AllTypes)scti.next()).typeCode;
                }
            }
        }
        return elemInfo;
    }

    protected Type m_elementType;

    public int getElemTypeLength() {
        return m_elemTypeLength;
    }

    public int getElemTypePrecision() {
        return m_elemTypePrecision;
    }

    public int getElemTypeScale() {
        return m_elemTypeScale;
    }

    public boolean isNChar() {
        return m_isNChar;
    }

    protected int m_elemTypeLength;
    protected int m_elemTypePrecision;
    protected int m_elemTypeScale;
    protected boolean m_isNChar;
}

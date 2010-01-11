/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

//javase imports
import java.sql.SQLException;
import java.util.Iterator;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllCollTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ElemInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewRow;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_COLL_TYPES;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_TYPES;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.OWNER;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.TYPE_NAME;

public class SqlCollectionType extends SqlType {

    protected int m_elemTypeLength;
    protected int m_elemTypePrecision;
    protected int m_elemTypeScale;
    protected boolean m_isNChar;

    public SqlCollectionType(SqlName sqlName, int typecode, boolean generateMe, SqlType parentType,
        SqlReflector reflector) {
        super(sqlName, typecode, generateMe, parentType, reflector);
        ((JavaName)sqlName.getLangName()).ungenPattern(sqlName.getSimpleName());
    }

    public SqlCollectionType(SqlName sqlName, TypeClass eleType, SqlReflector reflector)
        throws SQLException {
        super(sqlName, OracleTypes.TABLE, true, null, reflector);
        ((JavaName)sqlName.getLangName()).ungenPattern(sqlName.getSimpleName());
        m_elementType = eleType;
    }

    /**
     * Return the Type object that represents the component type of this collection type.
     */
    public TypeClass getComponentType() throws SQLException, PublisherException {
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
                    result = SqlReflector.UNKNOWN_TYPE;
                }
            }
            m_elementType = result;
        }
        return m_elementType;
    }

    protected ElemInfo getElemInfo() throws SQLException {
        ElemInfo elemInfo = null;
        SqlName sqlName = getSqlName();
        String schema = sqlName.getSchemaName();
        String type = sqlName.getTypeName();
        Iterator<ViewRow> scti = m_viewCache.getRows(ALL_COLL_TYPES, new String[0], new String[]{
            OWNER, TYPE_NAME}, new Object[]{schema, type}, new String[0]);
        if (scti.hasNext()) {
            AllCollTypes viewRow = (AllCollTypes)scti.next();
            m_isNChar = SqlReflector.NCHAR_CS.equals(viewRow.characterSetName);
            elemInfo = new ElemInfo(viewRow);
            if (SqlReflector.isNull(elemInfo.elemTypeMod)
                && !SqlReflector.isNull(elemInfo.elemTypeName)) {
                scti = m_viewCache.getRows(ALL_TYPES, new String[0],
                    new String[]{OWNER, TYPE_NAME}, new Object[]{elemInfo.elemTypeOwner,
                        elemInfo.elemTypeName}, new String[0]);
                if (scti.hasNext()) {
                    elemInfo.elemTypeMod = ((AllTypes)scti.next()).typeCode;
                }
            }
        }
        return elemInfo;
    }

    protected TypeClass m_elementType;

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
}

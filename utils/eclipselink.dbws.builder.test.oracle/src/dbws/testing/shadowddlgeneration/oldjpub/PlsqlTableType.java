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

import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_ARGUMENTS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OBJECT_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OVERLOAD;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OWNER;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.PACKAGE_NAME;

//javase imports
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Describe PL/SQL table type, including index-by tables
 */
public class PlsqlTableType extends SqlCollectionType {

    protected ElemInfo m_elemInfo;

    public PlsqlTableType(SqlName sqlName, int typeCode, ElemInfo elemInfo, SqlType elemType,
        int[] details, boolean generateMe, SqlType parentType, SqlReflector reflector) {
        super(sqlName, typeCode, generateMe, parentType, reflector);
        m_elemInfo = elemInfo;
        m_elementType = elemType;
        m_elemTypeLength = details[DETAILS_TYPE_LENGTH];
        m_elemTypePrecision = details[DETAILS_TYPE_PRECISION];
        m_elemTypeScale = details[DETAILS_TYPE_SCALE];
    }

    @Override
    protected ElemInfo getElemInfo() {
        return m_elemInfo;
    }

    public static ElemInfo getElemInfo(String _schema, String _typeName, String packageName,
        String methodName, String methodNo, ViewCache viewCache) throws SQLException {

        String schema = Util.getSchema(_schema, _typeName);
        String typeName = Util.getType(_schema, _typeName);
        if (typeName.indexOf('.') >= 0) {
            typeName = typeName.substring(typeName.indexOf('.') + 1);
        }

        Iterator<ViewRow> iter;
        if (packageName != null && packageName.length() > 0) {
            iter = viewCache.getRows(ALL_ARGUMENTS, new String[0], new String[]{OWNER, PACKAGE_NAME,
                OBJECT_NAME, OVERLOAD}, new Object[]{schema, packageName, methodName, methodNo},
            // new String[0]);
                new String[]{"SEQUENCE"});
        }
        else { // For toplevel publishing
            iter = viewCache.getRows(ALL_ARGUMENTS, new String[0], new String[]{OWNER}, new Object[]{schema},
                new String[0]);
        }
        PlsqlElemHelper[] info = PlsqlElemHelper.getPlsqlElemHelper(iter);

        // element type is either right before or after the table type
        int i0 = 0;
        int i1 = -1;
        for (i0 = 1; i0 < info.length - 1; i0++) {
            if (schema != null && schema.equals(info[i0].typeOwner) && typeName != null
                && typeName.equals(info[i0].typeSubname)) {
                if (info[i0 - 1].sequence == (info[i0].sequence + 1)
                    && info[i0 - 1].dataLevel == (info[i0].dataLevel + 1)
                    && info[i0 - 1].objectName.equals(info[i0].objectName)
                    && (info[i0 - 1].overload == info[i0].overload || (info[i0 - 1].overload != null && info[i0 - 1].overload
                        .equals(info[i0].overload)))) {
                    i1 = i0 - 1;
                    break;
                }
                else if (info[i0 + 1].sequence == (info[i0].sequence + 1)
                    && info[i0 + 1].dataLevel == (info[i0].dataLevel + 1)
                    && info[i0 + 1].objectName.equals(info[i0].objectName)
                    && (info[i0 - 1].overload == info[i0].overload || (info[i0 - 1].overload != null && info[i0 - 1].overload
                        .equals(info[i0].overload)))) {
                    i1 = i0 + 1;
                    break;
                }
            }
        }
        if (i1 == -1) {
            i0 = 0;
            if (schema != null && schema.equals(info[i0].typeOwner) && typeName != null
                && typeName.equals(info[i0].typeSubname)) {
                i1 = 1;
            }
            i0 = info.length - 1;
            if (schema != null && schema.equals(info[i0].typeOwner) && typeName != null
                && typeName.equals(info[i0].typeSubname)) {
                i1 = i0 - 1;
            }
        }

        if (i1 >= info.length || i1 < 0) {
            throw new java.sql.SQLException("Error reflecting element type for collection type "
                + typeName);
        }

        PlsqlElemInfo peti = new PlsqlElemInfo(info[i1]);
        return peti;
    }

    @Override
    public TypeClass getComponentType() {
        return m_elementType;
    }

    public static final int NUMBER_OF_DETAILS = 3;
    public static final int DETAILS_TYPE_LENGTH = 0;
    public static final int DETAILS_TYPE_PRECISION = 1;
    public static final int DETAILS_TYPE_SCALE = 2;

    public static TypeClass getComponentType(ElemInfo elemInfo, SqlReflector reflector, SqlType parentType,
        int[] details) throws SQLException, PublisherException {
        SqlType result = null;
        try {
            PlsqlElemInfo info = null;
            info = (PlsqlElemInfo)elemInfo;
            String elemTypeName = info.elemTypeName;
            String elemSchemaName = info.elemTypeOwner;
            String elemTypeMod = info.elemTypeMod;
            String elemPackageName = info.elemTypePackageName;
            String elemMethodName = info.elemTypeMethodName;
            String elemMethodNo = info.elemTypeMethodNo;
            int elemTypeSequence = info.elemTypeSequence;
            details[0] = info.elemTypeLength;
            details[1] = info.elemTypePrecision;
            details[2] = info.elemTypeScale;

            result = reflector.addPlsqlDBType(elemSchemaName, elemTypeName, null, elemTypeMod,
                false, elemPackageName, elemMethodName, elemMethodNo, elemTypeSequence, parentType); // this
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            if (result == null) {
                result = SqlReflector.UNKNOWN_TYPE;
            }
        }
        return result;
    }

    /*
     * Figure out the index type for PL/SQL INDEXBY TABLE
     */
    private static String getIndexType(SqlName sqlName, ViewCache viewCache) throws SQLException {
        return "NUMBER";
    }

    /**
     * Create a PL/SQL table type. If -plsqlindexbytable is set and whenever possible, the method
     * will return predefined scalar index-by table types, which will be mapped into Java arrays.
     *
     * @param sqlName
     * @param typeCode
     * @param elemInfo
     * @param elemType
     * @param details
     * @param generateMe
     * @param parentType
     * @param isGrandparent
     * @param options
     * @param reflector
     * @return either a newly defined PL/SQL Table type or a predefined scalar PL/SQL index-by table
     *         type
     * @throws SQLException
     * @throws PublisherException
     */

    public static SqlType newInstance(SqlName sqlName, int typeCode, ElemInfo elemInfo,
        SqlType elemType, int[] details, boolean generateMe, SqlType parentType,
        boolean isGrandparent, SqlReflector reflector) throws SQLException, PublisherException {
        /**
         * Identify toplevel scalar PL/SQL indexby table, to be mapped to predefined PL/SQL indexby
         * table type. Under the following situations, predefined scalar index-by table types cannot
         * be used: - in pre 10.2 compatible mode - when the PL/SQL table is used as a field of a
         * record (isGrandparent)
         */
        if (parentType != null
            && (!isGrandparent && parentType != null && parentType instanceof SqlPackageType)
            && typeCode == OracleTypes.PLSQL_INDEX_TABLE) {
            @SuppressWarnings("unused")
            String indexType = getIndexType(sqlName, reflector.getViewCache());
            /*
             * if ("NUMBER".equals(indexType)) { Map map = new JavaMap(elemType, reflector); SqlType
             * plsqlTableType = map.getPlsqlTableType(elemType); if (plsqlTableType != null) {
             * return plsqlTableType; } }
             */
        }
        // mapped to user-defined SQL table type
        return new PlsqlTableType(sqlName, typeCode, elemInfo, elemType, details, generateMe,
            parentType, reflector);
    }
}

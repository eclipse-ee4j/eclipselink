/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package  org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

//javase imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.MethodFilter;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.MethodInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ParamInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ResultInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.SingleColumnViewRow;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewRow;

public abstract class SqlTypeWithMethods extends SqlTypeWithFields {

    public SqlTypeWithMethods(SqlName sqlName, int typecode, boolean generateMe,
        SqlType parentType, MethodFilter signatureFilter, SqlReflector reflector)
        throws SQLException {
        super(sqlName, typecode, generateMe, parentType, reflector);
        m_methodFilter = signatureFilter;
    }

    /**
     * Returns an array of Method objects reflecting all the methods declared by this
     * SqlTypeWithMethods object. Returns an array of length 0 if the SqlTypeWithMethods declares no
     * methods
     */
    public List<ProcedureMethod> getDeclaredMethods() throws SQLException, PublisherException {
        if (m_methods == null) {
            m_methods = reflectMethods(getSqlName());
        }
        return m_methods;
    }

    public boolean hasMethods() throws SQLException, PublisherException {
        List<ProcedureMethod> m = getDeclaredMethods();
        return m != null && m.size() > 0;
    }

    private List<ProcedureMethod> reflectMethods(SqlName sqlName) throws SQLException, PublisherException {
        String schema = sqlName.getSchemaName();
        String type = sqlName.getTypeName();
        ArrayList<ProcedureMethod> methodl = new ArrayList<ProcedureMethod>();

        /* get method information */
        MethodInfo[] minfo = getMethodInfo(schema, type);
        for (int minfoi = 0; minfoi < minfo.length; minfoi++) {
            String methodName = minfo[minfoi].methodName;
            String methodType = minfo[minfoi].methodType;
            String methodNo = minfo[minfoi].methodNo;
            int results = minfo[minfoi].results;
            int parameters = minfo[minfoi].parameters;

            // Pre-approve the method, to avoid unwanted methods during Toplevel publishing.
            boolean preApproved = true;
            if (m_methodFilter != null) {
                preApproved = m_methodFilter.acceptMethod(new ProcedureMethod(methodName, null, -1, null,
                    null, null, null, null, 0), true);
            }
            if (!preApproved) {
                continue;
            }

            int modifiers;
            modifiers = PublisherModifier.PUBLIC;
            if (methodType.equals("MAP")) {
                modifiers = modifiers ^ PublisherModifier.MAP;
            }
            else if (methodType.equals("ORDER")) {
                modifiers = modifiers ^ PublisherModifier.ORDER;
            }

            TypeClass returnType = null;
            ResultInfo resultInfo = null;

            /* get return type information */
            if (results > 0) {
                resultInfo = getResultInfo(schema, type, methodName, methodNo);
                if (resultInfo != null) {
                    try {
                        String resultTypeOwner = resultInfo.resultTypeOwner;
                        String resultTypeName = resultInfo.resultTypeName;
                        String resultTypeSubname = resultInfo.resultTypeSubname;
                        String resultTypeMod = resultInfo.resultTypeMod;
                        boolean ncharFormOfUse = resultInfo.ncharFormOfUse;
                        String resultMethodName = resultInfo.methodName;
                        String resultMethodNo = resultInfo.methodNo;
                        int sequence = resultInfo.sequence;
                        returnType = m_reflector.addPlsqlDBType(resultTypeOwner, resultTypeName,
                            resultTypeSubname, resultTypeMod, ncharFormOfUse, type,
                            resultMethodName, resultMethodNo, sequence, this);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            /* get parameter information */
            int paramCount = parameters;
            List<TypeClass> paramTypes_v = new ArrayList<TypeClass>();
            List<String> paramNames_v = new ArrayList<String>();
            List<Integer> paramModes_v = new ArrayList<Integer>();
            List<Boolean> paramNCharFormOfUse_v = new ArrayList<Boolean>();
            int firstNoDefault = -1;
            boolean[] paramDefaults = new boolean[paramCount];
            if (paramCount > 0) {
                ParamInfo[] pinfo = null;
                pinfo = getParamInfo(schema, type, methodName, methodNo);
                String[] paramTypeOwner = new String[paramCount];
                String[] paramName = new String[paramCount];
                String[] paramTypeName = new String[paramCount];
                String[] paramTypeSubname = new String[paramCount];
                String[] paramTypeMod = new String[paramCount];
                String[] paramMode = new String[paramCount];
                boolean[] mcharFormOfUse = new boolean[paramCount];
                String[] paramMethodName = new String[paramCount];
                String[] paramMethodNo = new String[paramCount];
                int[] sequence = new int[paramCount];
                int[] objectId = new int[paramCount];
                for (int i = pinfo.length - 1; i >= 0; i--) {
                    paramTypeOwner[i] = pinfo[i].paramTypeOwner;
                    paramName[i] = pinfo[i].paramName;
                    paramTypeName[i] = pinfo[i].paramTypeName;
                    paramTypeSubname[i] = pinfo[i].paramTypeSubname;
                    paramTypeMod[i] = pinfo[i].paramTypeMod;
                    paramMode[i] = pinfo[i].paramMode;
                    mcharFormOfUse[i] = pinfo[i].ncharFormOfUse;
                    paramMethodName[i] = pinfo[i].methodName;
                    paramMethodNo[i] = pinfo[i].methodNo;
                    sequence[i] = pinfo[i].sequence;
                    objectId[i] = pinfo[i].objectId;
                }
                paramDefaults = new boolean[pinfo.length];
                for (int i = pinfo.length - 1; i >= 0; i--) {
                    paramDefaults[i] = hasDefault(objectId[i], paramMethodName[i], sequence[i],
                        paramMethodNo[i]);
                }
                for (int i = pinfo.length - 1; i >= 0; i--) {
                    if (!paramDefaults[i]) {
                        firstNoDefault = i;
                        break;
                    }
                }
                for (int i = 0; i < paramCount && paramMethodName[i] != null; i++) {
                    try {
                        paramNames_v.add(paramName[i]);
                        String mode = paramMode[i];
                        paramModes_v.add(Integer.valueOf((mode == null) ? ProcedureMethod.INOUT : (mode
                            .equals("IN") ? ProcedureMethod.IN : (mode.equals("OUT") ? ProcedureMethod.OUT
                            : ProcedureMethod.INOUT))));
                        paramTypes_v.add(m_reflector.addPlsqlDBType(paramTypeOwner[i],
                            paramTypeName[i], paramTypeSubname[i], paramTypeMod[i],
                            mcharFormOfUse[i], type, paramMethodName[i], paramMethodNo[i],
                            sequence[i], this));
                        paramNCharFormOfUse_v.add(Boolean.valueOf(mcharFormOfUse[i]));
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            int len = paramTypes_v.size();
            if (len != paramCount) {
                System.err.println("WARNING: incorrect parameter number for method " + methodName
                    + ". Expect " + paramCount + " with actual " + len);

            }

            TypeClass[] paramTypes = new TypeClass[len];
            String[] paramNames = new String[len];
            int[] paramModes = new int[len];
            boolean[] paramNCharFormOfUse = new boolean[len];
            for (int i = 0; i < len; i++) {
                paramTypes[i] = (TypeClass)(paramTypes_v.get(i));
                paramNames[i] = paramNames_v.get(i);
                paramModes[i] = paramModes_v.get(i).intValue();
                paramNCharFormOfUse[i] = paramNCharFormOfUse_v.get(i).booleanValue();
            }

            paramTypes = generateDefaultArgsHolderParamTypes(paramTypes, paramDefaults,
                paramNCharFormOfUse);
            ProcedureMethod method = null;
            for (int paramLen = firstNoDefault + 1; paramLen <= paramCount; paramLen++) {
                if (this instanceof SqlPackageType && returnType != null && resultInfo != null
                    && returnType.equals(SqlReflector.REF_CURSOR_TYPE)) {
                    method = new PlsqlCursorMethod(type, methodName, methodNo, modifiers,
                        resultInfo.sequence, paramTypes, paramNames, paramModes, paramDefaults,
                        paramLen, false, m_reflector);
                }
                else if (this instanceof SqlPackageType) {
                    method = new PlsqlMethod(methodName, methodNo, modifiers, returnType,
                        paramTypes, paramNames, paramModes, paramDefaults, paramLen);
                }
                else {
                    method = new ProcedureMethod(methodName, methodNo, modifiers, returnType, paramTypes,
                        paramNames, paramModes, paramDefaults, paramLen);
                }

                if (acceptMethod(method, false)) {
                    methodl.add(method);
                    if (returnType != null && resultInfo != null
                        && returnType.equals(SqlReflector.REF_CURSOR_TYPE)) {
                        method = new PlsqlCursorMethod(type, methodName, methodNo, modifiers,
                            resultInfo.sequence, paramTypes, paramNames, paramModes, paramDefaults,
                            paramLen, true, /* returnBeans */
                            m_reflector);
                        if (((PlsqlCursorMethod)method).getReturnColCount() != 0) {
                            methodl.add(method);
                        }
                    }
                }
            }
        }
        Collections.sort(methodl);
        return methodl;
    }

    protected abstract MethodInfo[] getMethodInfo(String schema, String name) throws SQLException;

    protected boolean acceptMethod(ProcedureMethod method, boolean preApprove) {
        boolean accept = true;
        if (m_methodFilter != null) {
            accept = m_methodFilter.acceptMethod(method, preApprove);
        }
        return accept;
    }

    protected abstract ResultInfo getResultInfo(String schema, String name, String method,
        String methodNo) throws SQLException;

    protected abstract ParamInfo[] getParamInfo(String schema, String name, String method,
        String methodNo) throws SQLException;

    @SuppressWarnings("unused")
    protected boolean hasDefault(int object_id, String methodName, int sequence, String overload)
        throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        final int objectIdIdx = 1;
        final int objectNameIdx = 2;
        final int seqIdx = 3;
        int hasDefaultInt = 0;

        /*
         * Connection conn = null; PreparedStatement stmt = null; ResultSet rs = null;
         *
         * conn = m_reflector.getConnection();
         *
         * if (overload == null || overload.equals("")) { stmt =conn.prepareStatement(
         * "SELECT DEFAULTED FROM ALL_ARGUMENTS WHERE OBJECT_ID=:1 AND OBJECT_NAME=:2 AND SEQUENCE=:3 AND OVERLOAD IS NULL"
         * ); } else { stmt =conn.prepareStatement(
         * "SELECT DEFAULTED FROM ALL_ARGUMENTS WHERE OBJECT_ID=:1 AND OBJECT_NAME=:2 AND SEQUENCE=:3 AND OVERLOAD='"
         * + overload + "'"); } stmt.setInt(objectIdIdx, object_id); stmt.setString(objectNameIdx,
         * methodName); stmt.setInt(seqIdx, sequence); try { rs = stmt.executeQuery(); if
         * (rs.next()) { String defaulted = rs.getString(1); hasDefaultInt =
         * "Y".equalsIgnoreCase(defaulted) ? 1 : 0; }
         */
        try {
            Iterator<ViewRow> rowIter;
            if (overload == null || overload.equals("")) {
                rowIter = m_viewCache.getRows(Util.ALL_ARGUMENTS, new String[]{"DEFAULTED"},
                    new String[]{"OBJECT_ID", "OBJECT_NAME", "SEQUENCE", "OVERLOAD"}, new Object[]{
                        Integer.valueOf(object_id), methodName, Integer.valueOf(sequence), null},
                    new String[0]);
            }
            else {
                rowIter = m_viewCache.getRows(Util.ALL_ARGUMENTS, new String[]{"DEFAULTED"},
                    new String[]{"OBJECT_ID", "OBJECT_NAME", "SEQUENCE", "OVERLOAD"}, new Object[]{
                        Integer.valueOf(object_id), methodName, Integer.valueOf(sequence), overload},
                    new String[0]);
            }
            if (rowIter.hasNext()) {
                SingleColumnViewRow row = (SingleColumnViewRow)rowIter.next();
                String defaulted = row.getValue();
                hasDefaultInt = "Y".equalsIgnoreCase(defaulted) ? 1 : 0;
            }
            else {
                throw new SQLException(
                    "Pre-10.2 database do not support DEFAULTED in ALL_ARGUMENTS");
            }
        }
        catch (Exception se) { // SQLException: ORA-00904: "DEFAULTED": invalid identifier
            // se.printStackTrace();
            // conn = m_reflector.getConnection();
            try {
                // DEFAULTED ONLY EXISTS in Database 10.2
                final int oidIdx = 1;
                final int methodNameIdx = 2;
                final int sequenceIdx = 3;
                final int overloadIdx = 4;

                /*
                 * if (stmt != null) { stmt.close(); } if (rs != null) { rs.close(); }
                 *
                 * stmt =
                 * conn.prepareStatement("SELECT SYS.SQLJUTL.HAS_DEFAULT(:1, :2, :3, :4) FROM DUAL"
                 * ); stmt.setInt(oidIdx, object_id); stmt.setString(methodNameIdx, methodName);
                 * stmt.setInt(sequenceIdx, sequence); if (overload == null || overload.equals(""))
                 * { stmt.setInt(overloadIdx, 0); } else { stmt.setInt(overloadIdx,
                 * Integer.parseInt(overload)); } rs = stmt.executeQuery(); boolean hasNext =
                 * rs.next(); if (hasNext) { hasDefaultInt = rs.getInt(1); }
                 */

                String sqljutl = "SYS.SQLJUTL.HAS_DEFAULT(" + object_id + ", " + "'"
                    + methodName.toUpperCase() + "', " + sequence + ","
                    + ((overload == null || overload.equals("")) ? "0" : overload) + ")";
                Iterator<ViewRow> rowIter = m_viewCache.getRows(Util.DUAL, new String[]{sqljutl},
                    new String[0], new Object[0], new String[0]);

                if (rowIter.hasNext()) {
                    SingleColumnViewRow row = (SingleColumnViewRow)rowIter.next();
                    if (row.getValue() != null) {
                        hasDefaultInt = Integer.parseInt(row.getValue());
                    }
                }
            }
            catch (Exception e8) {
                e8.printStackTrace();
                System.err
                    .println("WARNING: please install SYS.SQLJUTL for appropriate treatement of PL/SQL default arguments.");
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (SQLException e) {
                // Close resources, ignore exceptions.
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                // Close resources, ignore exceptions.
            }
        }
        if (hasDefaultInt == 1) {
            return true;
        }
        return false;
    }

    TypeClass[] generateDefaultArgsHolderParamTypes(TypeClass[] paramTypes, boolean[] paramDefaults,
        boolean[] ncharFormOfUse) throws SQLException, PublisherException {

        TypeClass[] defaultParamTypes = paramTypes;
        boolean hasDefault = false;
        for (int i = 0; i < paramDefaults.length; i++) {
            if (paramDefaults[i]) {
                hasDefault = true;
            }
        }
        if (hasDefault) {
            defaultParamTypes = new TypeClass[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramDefaults[i]) {
                    defaultParamTypes[i] = m_reflector.addDefaultArgsHolderType(
                        (SqlType)paramTypes[i], getSqlName().getSimpleName(), this,
                        ncharFormOfUse[i]);
                }
                else {
                    defaultParamTypes[i] = paramTypes[i];
                }
            }
        }
        return defaultParamTypes;
    }

    protected List<ProcedureMethod> m_methods;
    protected MethodFilter m_methodFilter = null;
}
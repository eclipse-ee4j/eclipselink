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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * Instances of the class </code>SerializableType</code> represent Java serializable types.
 */
public class SerializableType extends SqlType {
    /**
     * Construct a SqlRef Type
     */
    public SerializableType(String serType, SqlReflector reflector) {
        super(new SqlName("", serType, false, reflector), OracleTypes.BLOB, false, null, reflector);
        ((SqlName)m_name).setLangName(null, serType, null, null, null, null, null, null, true);
        String eleType = serType;
        int eleIdx = -1;
        m_arrayDim = 0;
        while ((eleIdx = eleType.lastIndexOf("[]")) > -1) {
            m_arrayDim++;
            eleType = eleType.substring(0, eleIdx);
        }
    }

    public int hashCode() {
        return m_name.hashCode() ^ 1;
    }

    /**
     * Returns the fully-qualified name of the type represented by this Type object, as a String.
     */
    public String getName() { // BLOB
        // return m_name.getSimpleName();
        return SqlReflector.BLOB_TYPE.getSqlName().getSimpleName();
    }

    public String getTargetTypeName() {
        return getName();
    }

    public String getTypeName() {
        return getName();
    }

    public String getFullDeclClass() {
        // MySerializableObject
        String pkg = ((SqlName)m_name).getDeclPackage();
        pkg = (pkg == null || pkg.equals("")) ? "" : pkg + ".";
        return pkg + ((SqlName)m_name).getDeclClass();
    }

    public String getJdbcType(Typemap map) {
        // java.sql.Blob or oracle.sql.BLOB
        return map.writeTypeName(SqlReflector.BLOB_TYPE);
    }

    public String getJdbcClass() {
        // oracle.sql.BLOB
        return "oracle.sql.BLOB";
    }

    public boolean isArray() {
        return m_arrayDim > 0;
    }

    public int getArrayDim() {
        return m_arrayDim;
    }

    private int m_arrayDim;
}

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
package dbws.testing.shadowddlgeneration.oldjpub;

public abstract class JavaType extends TypeClass {

    protected TypeClass m_sqlType; // null for CURSOR element type

    /* Create a predefined Java type */
    public JavaType(String typeName, TypeClass sqlType) {
        this(new JavaName(null, typeName), sqlType);
    };

    /* Create an added Java type */
    public JavaType(JavaName javaName, TypeClass sqlType) {
        super(javaName, OracleTypes.JAVA_TYPE, false);
        m_sqlType = sqlType;
    };

    public JavaName getJavaName() {
        return (JavaName)m_name;
    }

    public String getTypeName() {
        return m_name.getUseClass();
    }

    public String getTypeName(Typemap map) {
        return getTypeName();
    }

    public int getTypecode() {
        if (m_sqlType != null) {
            return m_sqlType.m_typecode;
        }
        return m_typecode;
    }
}

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

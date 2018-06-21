/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

public class JavaArrayType extends JavaType {

    protected TypeClass m_eleType;
    protected SqlReflector m_reflector;

    public JavaArrayType(TypeClass eleType, SqlReflector reflector, TypeClass sqlType) {
        super((((JavaType)eleType).getTypeName() + "[]"), sqlType);
        m_eleType = eleType;
        m_reflector = reflector;
    }

    public String getTypeName(Typemap map) {
        if (m_eleType instanceof JavaType) {
            return getTypeName();
        }
        return map.writeTypeName(m_eleType) + "[]";
    }

    public TypeClass getEleType() {
        return m_eleType;
    }

}

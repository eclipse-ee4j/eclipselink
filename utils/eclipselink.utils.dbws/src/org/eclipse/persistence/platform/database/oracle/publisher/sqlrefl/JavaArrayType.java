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

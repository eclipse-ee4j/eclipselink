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

/**
 * Instances of the class </code>SqlRefType</code> represent SQL REF types.
 */
public class SqlRefType extends SqlType {

    protected SqlType m_refeeType;

    /**
     * Construct a SqlRef Type
     */
    SqlRefType(SqlName sqlName, SqlType refeeType, SqlType parentType, boolean generateMe,
        SqlReflector reflector) {
        super(null, OracleTypes.REF, false, parentType, reflector);

        m_refeeType = refeeType;
    }

    public int hashCode() {
        return m_refeeType.hashCode() ^ 1;
    }

    /**
     * Returns the fully-qualified name of the type represented by this Type object, as a String.
     */
    public String getName() {
        return "REF " + m_refeeType.getName();
    }

    public String getTargetTypeName() {
        if (m_refeeType instanceof SqlType) {
            return "REF " + ((SqlType)m_refeeType).getTargetTypeName();
        }
        else {
            return getName();
        }
    }

    public String getTypeName() {
        if (m_refeeType instanceof SqlType) {
            return "REF " + ((SqlType)m_refeeType).getTypeName();
        }
        else {
            return getName();
        }
    }

    /**
     * If this Type has a component type, return the Type object that represents the component type;
     * otherwise returns null.
     */
    public TypeClass getComponentType() {
        return m_refeeType;
    }
}

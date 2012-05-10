/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

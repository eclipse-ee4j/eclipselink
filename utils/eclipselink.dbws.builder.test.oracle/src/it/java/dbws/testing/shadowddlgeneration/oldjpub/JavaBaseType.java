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

//javase imports
import java.util.List;

public class JavaBaseType extends JavaType {

    protected List<AttributeField> m_fields;
    protected List<ProcedureMethod> m_methods;

    /* Create a predefined Java type */
    public JavaBaseType(String typeName, List<AttributeField> fields, List<ProcedureMethod> methods,
        TypeClass sqlType) {
        super(typeName, sqlType);
        m_fields = fields;
        m_methods = methods;
        // System.out.println("[JavaBaseType] " + m_fields.length); //D+
    };

    /* Create an added Java type */
    public JavaBaseType(JavaName javaName, List<AttributeField> fields, List<ProcedureMethod> methods,
        TypeClass sqlType) {
        super(javaName, sqlType);
        m_fields = fields;
        m_methods = methods;
        // System.out.println("[JavaBaseType] " + m_fields.length); //D+
    };

    public List<AttributeField> getFields(boolean publishedOnly) {
        return m_fields;
    }

    public List<AttributeField> getDeclaredFields(boolean publishedOnly) {
        return m_fields;
    }

    public List<ProcedureMethod> getDeclaredMethods() {
        return m_methods;
    }

    public boolean hasFields() {
        return m_fields != null && m_fields.size() > 0;
    }

    public boolean hasMethods() {
        return m_methods != null && m_methods.size() > 0;
    }
}

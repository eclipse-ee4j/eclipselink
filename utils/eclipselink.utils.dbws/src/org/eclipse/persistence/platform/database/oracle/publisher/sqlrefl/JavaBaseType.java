/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
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

public class JavaBaseType extends JavaType {
    /* Create a predefined Java type */
    public JavaBaseType(String typeName, AttributeField[] fields, ProcedureMethod[] methods, TypeClass sqlType) {
        super(typeName, sqlType);
        m_fields = fields;
        m_methods = methods;
        // System.out.println("[JavaBaseType] " + m_fields.length); //D+
    };

    /* Create an added Java type */
    public JavaBaseType(JavaName javaName, AttributeField[] fields, ProcedureMethod[] methods, TypeClass sqlType) {
        super(javaName, sqlType);
        m_fields = fields;
        m_methods = methods;
        // System.out.println("[JavaBaseType] " + m_fields.length); //D+
    };

    public AttributeField[] getFields(boolean publishedOnly) {
        return m_fields;
    }

    public AttributeField[] getDeclaredFields(boolean publishedOnly) {
        return m_fields;
    }

    public ProcedureMethod[] getDeclaredMethods() {
        return m_methods;
    }

    public boolean hasFields() {
        return m_fields != null && m_fields.length > 0;
    }

    public boolean hasMethods() {
        return m_methods != null && m_methods.length > 0;
    }

    protected AttributeField[] m_fields;
    protected ProcedureMethod[] m_methods;
}

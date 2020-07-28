/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Rick Barkhouse - 2.2 - Initial implementation
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.lang.reflect.Modifier;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaConstructor</code> implementation used when bootstrapping
 * a <code>DynamicJAXBContext</code> from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 *    <li>Provide Constructor information to the <code>JavaModel</code>.</li>
 * </ul>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaConstructor
 */
public class OXMJavaConstructorImpl implements JavaConstructor {

    private JavaClass owningClass;

    /**
     * Construct a new instance of <code>OXMJavaConstructorImpl</code>.
     *
     * @param owner - the <code>JavaClass</code> this constructor belongs to.
     */
    public OXMJavaConstructorImpl(JavaClass owner) {
        this.owningClass = owner;
    }

    public int getModifiers() {
        return 0;
    }

    public String getName() {
        return null;
    }

    public JavaClass getOwningClass() {
        return this.owningClass;
    }

    public JavaClass[] getParameterTypes() {
        return new JavaClass[] {};
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    /**
     * Not supported.
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

}

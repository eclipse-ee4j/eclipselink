/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public JavaClass getOwningClass() {
        return this.owningClass;
    }

    @Override
    public JavaClass[] getParameterTypes() {
        return new JavaClass[] {};
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    @Override
    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    /**
     * Not supported.
     */
    @Override
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

}

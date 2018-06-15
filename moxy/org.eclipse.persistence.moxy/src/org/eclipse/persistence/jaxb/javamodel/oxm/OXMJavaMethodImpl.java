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

import java.util.Collection;

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaMethod</code> implementation used when bootstrapping
 * a <code>DynamicJAXBContext</code> from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 *    <li>Provide Method information to the <code>JavaModel</code>.</li>
 * </ul>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaMethod
 */
public class OXMJavaMethodImpl implements JavaMethod {

    private String name;
    private JavaClass owningClass;
    private JavaClass returnType;

    /**
     * Construct a new instance of <code>OXMJavaMethodImpl</code>.
     *
     * @param methodName - this method's name
     * @param returnType - this method's return type as a <code>JavaClass</code>.
     * @param owner - the <code>JavaClass</code> this method belongs to.
     */
    public OXMJavaMethodImpl(String methodName, JavaClass returnType, JavaClass owner) {
        this.name = methodName;
        this.owningClass = owner;
        this.returnType = returnType;
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public JavaClass getOwningClass() {
        return owningClass;
    }

    @Override
    public JavaClass[] getParameterTypes() {
        return null;
    }

    @Override
    public JavaClass getReturnType() {
        return this.returnType;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public JavaAnnotation getAnnotation(JavaClass arg0) {
        return null;
    }

    @Override
    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    @Override
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        return null;
    }

    @Override
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        return null;
    }

    @Override
    public boolean isBridge() {
        return false;
    }
}

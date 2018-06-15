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
//     Rick Barkhouse - 2.2 - initial implementation
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.lang.reflect.Modifier;
import java.util.Collection;

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaField</code> implementation used when bootstrapping
 * a <code>DynamicJAXBContext</code> from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 *    <li>Provide Field information to the <code>JavaModel</code>.</li>
 * </ul>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaField
 */
public class OXMJavaFieldImpl implements JavaField {

    private String fieldName;
    private String fieldTypeName;
    private JavaClass owningClass;

    /**
     * Construct a new instance of <code>OXMJavaFieldImpl</code>.
     *
     * @param aFieldName - this fields's name
     * @param aFieldTypeName - this field's type as a <code>String</code>.
     * @param owner - the <code>JavaClass</code> this field belongs to.
     */
    public OXMJavaFieldImpl(String aFieldName, String aFieldTypeName, JavaClass owner) {
        this.fieldName = aFieldName;
        this.fieldTypeName = aFieldTypeName;
        this.owningClass = owner;
    }

    @Override
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        return null;
    }

    @Override
    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    @Override
    public String getName() {
        return this.fieldName;
    }

    public JavaClass getOwningClass() {
        return this.owningClass;
    }

    @Override
    public JavaClass getResolvedType() {
        return ((OXMJavaClassImpl) this.owningClass).getJavaModel().getClass(this.fieldTypeName);
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
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

    /**
     * Not supported.
     */
    @Override
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    @Override
    public boolean isEnumConstant() {
        return getOwningClass().isEnum();
    }

    @Override
    public JavaAnnotation getDeclaredAnnotation(JavaClass aClass) {
        return getAnnotation(aClass);
    }

    @Override
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        return getAnnotations();
    }

}

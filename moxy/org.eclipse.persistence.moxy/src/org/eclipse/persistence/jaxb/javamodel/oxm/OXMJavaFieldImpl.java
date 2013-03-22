/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.2 - initial implementation
 ******************************************************************************/
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
 * <ul>
 *    <li>Provide Field information to the <code>JavaModel</code>.</li>
 * </ul>
 * </p>
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

    public JavaAnnotation getAnnotation(JavaClass aClass) {
        return null;
    }

    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    public String getName() {
        return this.fieldName;
    }

    public JavaClass getOwningClass() {
        return this.owningClass;
    }

    public JavaClass getResolvedType() {
        return ((OXMJavaClassImpl) this.owningClass).getJavaModel().getClass(this.fieldTypeName);
    }

    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
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

    /**
     * Not supported.
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    public boolean isEnumConstant() {
        return getOwningClass().isEnum();
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass aClass) {
        return getAnnotation(aClass);
    }

    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        return getAnnotations();
    }

}
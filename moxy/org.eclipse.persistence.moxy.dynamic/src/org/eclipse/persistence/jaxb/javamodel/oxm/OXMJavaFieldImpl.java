/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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

public class OXMJavaFieldImpl implements JavaField {

    private String fieldName;
    private JavaClass owningClass;

    public OXMJavaFieldImpl(String aFieldName, JavaClass owner) {
        this.fieldName = aFieldName;
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
        return new OXMJavaClassImpl("java.lang.Object");
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
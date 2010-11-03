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
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.lang.reflect.Modifier;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;

public class OXMJavaConstructorImpl implements JavaConstructor {

    private JavaClass owningClass;
	
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

    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

}
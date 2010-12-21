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

import java.util.Collection;

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

public class OXMJavaPackageImpl implements JavaPackage {

    protected String packageName;

    public OXMJavaPackageImpl(String aPackage) {
        this.packageName = aPackage;
    }

    public JavaAnnotation getAnnotation(JavaClass aClass) {
        return null;
    }

    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    public String getName() {
        return this.packageName;
    }

    public String getQualifiedName() {
        return getName();
    }

    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

}
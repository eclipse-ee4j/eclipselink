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
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.oxm;

import java.util.Collection;

import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaPackage</code> implementation used when bootstrapping
 * a <code>DynamicJAXBContext</code> from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Provide Package information to the <code>JavaModel</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaPackage
 */
public class OXMJavaPackageImpl implements JavaPackage {

    protected String packageName;

    /**
     * Construct a new instance of <code>OXMJavaPackageImpl</code>.
     *
     * @param aPackage - the name of this <code>JavaPackage</code>.
     */
    public OXMJavaPackageImpl(String aPackage) {
        this.packageName = aPackage;
    }

    /**
     * If this <code>JavaPackage</code> is annotated with an <code>Annotation</code> matching <code>aClass</code>,
     * return its <code>JavaAnnotation</code> representation.
     *
     * @param aClass a <code>JavaClass</code> representing the <code>Annotation</code> to look for.
     *
     * @return always returns <code>null</code> as <code>JavaTypes</code> do not have <code>Annotations</code>.
     */
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        return null;
    }

    /**
     * Return all of the <code>Annotations</code> for this <code>JavaPackage</code>.
     *
     * @return always returns <code>null</code> as <code>JavaTypes</code> do not have <code>Annotations</code>.
     */
    public Collection<JavaAnnotation> getAnnotations() {
        return null;
    }

    /**
     * Returns the name of this <code>JavaPackage</code>.
     *
     * @return the <code>String</code> name of this <code>JavaPackage</code>.
     */
    public String getName() {
        return this.packageName;
    }

    /**
     * Returns the fully-qualified name of this <code>JavaPackage</code>.
     *
     * @return the <code>String</code> name of this <code>JavaPackage</code>.
     */
    public String getQualifiedName() {
        return getName();
    }

    /**
     * Not supported.
     */
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0) {
        throw new UnsupportedOperationException("getDeclaredAnnotation");
    }

    /**
     * Not supported.
     */
    public Collection<JavaAnnotation> getDeclaredAnnotations() {
        throw new UnsupportedOperationException("getDeclaredAnnotations");
    }

}
/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.xjc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaAnnotation;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaPackage;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JPackage;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaPackage</code> implementation wrapping XJC's <code>JPackage</code>.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Provide <code>Package</code> information from the underlying <code>JPackage</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaPackage
 */
public class XJCJavaPackageImpl implements JavaPackage {

    protected JPackage xjcPackage;
    private DynamicClassLoader dynamicClassLoader;

    private static Field JPACKAGE_ANNOTATIONS = null;
    static {
        try {
            JPACKAGE_ANNOTATIONS = PrivilegedAccessHelper.getDeclaredField(JPackage.class, "annotations", true);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }
    }

    /**
     * Construct a new instance of <code>XJCJavaPackageImpl</code>.
     *
     * @param jPackage - the XJC <code>JPackage</code> to be wrapped.
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     */
    public XJCJavaPackageImpl(JPackage jPackage, DynamicClassLoader loader) {
        this.xjcPackage = jPackage;
        this.dynamicClassLoader = loader;
    }

    /**
     * If this <code>JavaPackage</code> is annotated with an <code>Annotation</code> matching <code>aClass</code>,
     * return its <code>JavaAnnotation</code> representation.
     *
     * @param aClass a <code>JavaClass</code> representing the <code>Annotation</code> to look for.
     *
     * @return the <code>JavaAnnotation</code> represented by <code>aClass</code>, if one exists, otherwise return <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public JavaAnnotation getAnnotation(JavaClass aClass) {
        if (aClass != null) {

            Collection<JAnnotationUse> annotations = null;
            try {
                annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JPACKAGE_ANNOTATIONS, xjcPackage);
            } catch (Exception e) {
                throw JAXBException.errorCreatingDynamicJAXBContext(e);
            }

            if (annotations == null) {
                return null;
            }

            for (JAnnotationUse annotationUse : annotations) {
                XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
                if (xjcAnnotation.getJavaAnnotationClass().getCanonicalName().equals(aClass.getQualifiedName())) {
                    return xjcAnnotation;
                }
            }
            // Didn't find annotation so return null
            return null;
        }
        // aClass was null so return null
        return null;
    }

    /**
     * Return all of the <code>Annotations</code> for this <code>JavaPackage</code>.
     *
     * @return A <code>Collection</code> containing this <code>JavaPackage's</code> <code>JavaAnnotations</code>.
     */
    @SuppressWarnings("unchecked")
    public Collection<JavaAnnotation> getAnnotations() {
        ArrayList<JavaAnnotation> annotationsList = new ArrayList<JavaAnnotation>();

        Collection<JAnnotationUse> annotations = null;
        try {
            annotations = (Collection<JAnnotationUse>) PrivilegedAccessHelper.getValueFromField(JPACKAGE_ANNOTATIONS, xjcPackage);
        } catch (Exception e) {
            throw JAXBException.errorCreatingDynamicJAXBContext(e);
        }

        if (annotations == null) {
            return annotationsList;
        }

        for (JAnnotationUse annotationUse : annotations) {
            XJCJavaAnnotationImpl xjcAnnotation = new XJCJavaAnnotationImpl(annotationUse, dynamicClassLoader);
            annotationsList.add(xjcAnnotation);
        }
        return annotationsList;
    }

    /**
     * Returns the name of this <code>JavaPackage</code>.
     *
     * @return the <code>String</code> name of this <code>JavaPackage</code>.
     */
    public String getName() {
        if (xjcPackage != null){
            return xjcPackage.name();
        }
        return null;
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
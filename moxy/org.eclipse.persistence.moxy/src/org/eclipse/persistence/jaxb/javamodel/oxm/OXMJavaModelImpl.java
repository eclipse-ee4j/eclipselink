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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaModel</code> implementation backed by a collection of MOXY's
 * <code>xmlmodel.JavaClasses</code>.  Used when bootstrapping a <code>DynamicJAXBContext</code>
 * from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Return a <code>JavaClass</code> based on a <code>Class</code> or <code>Class</code> name.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModel
 */
public class OXMJavaModelImpl extends JavaModelImpl implements JavaModel  {

    private Map<String, JavaClass> javaModelClasses = new HashMap<String, JavaClass>();

    /**
     * Construct a new instance of <code>OXMJavaModelImpl</code>.
     *
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     * @param javaClasses - an array of <code>JavaClasses</code> for which to generate mappings.
     *
     */
    public OXMJavaModelImpl(ClassLoader loader, JavaClass[] javaClasses) {
        super(loader);

        for (int i = 0; i < javaClasses.length; i++) {
            this.javaModelClasses.put(javaClasses[i].getQualifiedName(), javaClasses[i]);
        }
    }

    /**
     * Obtain the <code>JavaClass</code> given the corresponding Java <code>Class</code>.
     *
     * @param jClass - the Java <code>Class</code> to search for.
     *
     * @return the <code>JavaClass</code> corresponding to <code>jClass</code>.
     */
    public JavaClass getClass(Class<?> jClass) {
        if (jClass == null) {
            return null;
        }

        String className = jClass.getCanonicalName();
        JavaClass cachedClass = this.javaModelClasses.get(className);

        if (cachedClass != null) {
            return cachedClass;
        }

        // try actually finding the class, might be concrete
        try {
            if (jClass.isArray()) {
                className = jClass.getName();
            }
            Class<?> realClass = PrivilegedAccessHelper.getClassForName(className, true, classLoader);
            if (realClass != null) {
                JavaModelImpl jm = new JavaModelImpl(classLoader);
                JavaClassImpl jc = new JavaClassImpl(realClass, jm);
                return jc;
            }
        } catch (Exception e) {
        }

        return new OXMJavaClassImpl(jClass.getCanonicalName());
    }

    /**
     * Obtain the <code>JavaClass</code> given the corresponding Java <code>Class'</code> name.
     *
     * @param className - the name of the Java <code>Class</code> to search for.
     *
     * @return the <code>JavaClass</code> corresponding to <code>className</code>.
     */
    public JavaClass getClass(String className) {
        if (className == null) {
            return null;
        }

        // javax.xml.bind.annotation.XmlElement.DEFAULT
        if (className.contains(DEFAULT)) {
            return getClass(JAVA_LANG_OBJECT);
        }

        JavaClass cachedClass = this.javaModelClasses.get(className);

        if (cachedClass != null) {
            return cachedClass;
        }

        // try actually finding the class, might be concrete
        try {
            Class<?> realClass = PrivilegedAccessHelper.getClassForName(className, true, classLoader);
            if (realClass != null) {
                JavaModelImpl jm = new JavaModelImpl(this.classLoader);
                JavaClassImpl jc = new JavaClassImpl(realClass, jm);
                return jc;
            }
        } catch (Exception e) {
        }

        return new OXMJavaClassImpl(className);
    }

    /**
     * Returns this <code>JavaModel's</code> <code>ClassLoader</code>.
     *
     * @return the <code>ClassLoader</code> used by this <code>JavaModel</code>.
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * Returns this <code>JavaModel's</code> <code>JaxbClassLoader</code>, which
     * should be the parent <code>ClassLoader</code>.
     *
     * @return the <code>JaxbClassLoader</code> used by this <code>JavaModel</code>.
     */
    public JaxbClassLoader getJaxbClassLoader() {
        ClassLoader parent = getClassLoader().getParent();
        if (parent instanceof JaxbClassLoader) {
            return (JaxbClassLoader) parent;
        }

        return null;
    }

    // ========================================================================

    private static String DEFAULT = "DEFAULT";
    private static String JAVA_LANG_OBJECT = "java.lang.Object";


}
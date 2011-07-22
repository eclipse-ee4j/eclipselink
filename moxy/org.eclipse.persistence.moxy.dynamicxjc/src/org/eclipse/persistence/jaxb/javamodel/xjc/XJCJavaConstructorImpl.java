/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.lang.reflect.Modifier;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaConstructor;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaConstructor</code> implementation wrapping XJC's <code>JMethod</code>.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Provide <code>Constructor</code> information from the underlying <code>JMethod</code>.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaConstructor
 */
public class XJCJavaConstructorImpl implements JavaConstructor {

    private JMethod xjcConstructor;
    private JCodeModel jCodeModel;
    private DynamicClassLoader dynamicClassLoader;
    private JavaClass owningClass;

    /**
     * Construct a new instance of <code>XJCJavaConstructorImpl</code>.
     *
     * @param constructor - the XJC <code>JMethod</code> to be wrapped.
     * @param codeModel - the XJC <code>JCodeModel</code> this constructor belongs to.
     * @param loader - the <code>ClassLoader</code> used to bootstrap the <code>DynamicJAXBContext</code>.
     * @param owner - the <code>JavaClass</code> this constructor belongs to.
     */
    public XJCJavaConstructorImpl(JMethod constructor, JCodeModel codeModel, DynamicClassLoader loader, JavaClass owner) {
        this.xjcConstructor = constructor;
        this.jCodeModel = codeModel;
        this.dynamicClassLoader = loader;
        this.owningClass = owner;
    }

    /**
     * Returns the Java language modifiers for this <code>JavaConstructor</code>, encoded in an integer.
     *
     * @return the <code>int</code> representing the modifiers for this constructor.
     *
     * @see java.lang.reflect.Modifier
     */
    public int getModifiers() {
        return xjcConstructor.mods().getValue();
    }

    /**
     * Returns the name of this <code>JavaConstructor</code>.
     *
     * @return the <code>String</code> name of this <code>JavaConstructor</code>.
     */
    public String getName() {
        return xjcConstructor.name();
    }

    /**
     * Returns the array of parameters for this <code>JavaConstructor</code>.
     *
     * @return a <code>JavaClass[]</code> representing the argument types for this constructor.
     */
    public JavaClass[] getParameterTypes() {
        JType[] params = xjcConstructor.listParamTypes();
        JavaClass[] paramArray = new JavaClass[params.length];

        for (int i = 0; i < params.length; i++) {
            JavaClass dc;
            if (((XJCJavaClassImpl) getOwningClass()).getJavaModel() != null) {
                dc = ((XJCJavaClassImpl) getOwningClass()).getJavaModel().getClass(params[i].fullName());
            } else {
                dc = new XJCJavaClassImpl((JDefinedClass) params[i], jCodeModel, dynamicClassLoader);
            }

            paramArray[i] = dc;
        }
        return paramArray;
    }

    /**
     * Indicates if this <code>JavaConstructor</code> is <code>abstract</code>.
     *
     * @return <code>true</code> if this <code>JavaConstructor</code> is <code>abstract</code>, otherwise <code>false</code>.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    /**
     * Indicates if this <code>JavaConstructor</code> is <code>private</code>.
     *
     * @return <code>true</code> if this <code>JavaConstructor</code> is <code>private</code>, otherwise <code>false</code>.
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    /**
     * Indicates if this <code>JavaConstructor</code> is <code>protected</code>.
     *
     * @return <code>true</code> if this <code>JavaConstructor</code> is <code>protected</code>, otherwise <code>false</code>.
     */
    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    /**
     * Indicates if this <code>JavaConstructor</code> is <code>public</code>.
     *
     * @return <code>true</code> if this <code>JavaConstructor</code> is <code>public</code>, otherwise <code>false</code>.
     */
    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }

    /**
     * Indicates if this <code>JavaConstructor</code> is <code>static</code>.
     *
     * @return <code>true</code> if this <code>JavaConstructor</code> is <code>static</code>, otherwise <code>false</code>.
     */
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    /**
     * Indicates if this <code>JavaConstructor</code> is <code>final</code>.
     *
     * @return <code>true</code> if this <code>JavaConstructor</code> is <code>final</code>, otherwise <code>false</code>.
     */
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    /**
     * Not supported.
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException("isSynthetic");
    }

    /**
     * Returns the <code>JavaClass</code> which contains this constructor.
     *
     * @return <code>JavaClass</code> representing the owner of this <code>JavaConstructor</code>.
     */
    public JavaClass getOwningClass() {
        return owningClass;
    }

    /**
     * Set the <code>JavaClass</code> which contains this constructor.
     *
     * @param owningClass the <code>JavaClass</code> representing the owner of this <code>JavaConstructor</code>.
     */
    public void setOwningClass(JavaClass owningClass) {
        this.owningClass = owningClass;
    }

}
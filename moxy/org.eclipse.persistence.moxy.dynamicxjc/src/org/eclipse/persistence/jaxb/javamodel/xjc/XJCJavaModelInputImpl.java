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

import java.lang.reflect.Type;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaModelInput</code> implementation for XJC.  Used when
 * bootstrapping a <code>DynamicJAXBContext</code> from an XML Schema.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Create an array of <code>JavaClass</code> instances from an array of <code>Classes/JavaClasses/Types/TypeMappingInfos</code>.</li>
 *    <li>Return an array of <code>JavaClass</code> objects to be used by the generator.</li>
 *    <li>Return the <code>JavaModel</code> to be used during generation.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.1
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModelInput
 */
public class XJCJavaModelInputImpl implements JavaModelInput {

    private JavaClass[] jClasses;
    private JavaModel jModel;

    /**
     * Construct a new instance of <code>XJCJavaModelInputImpl</code>.
     *
     * @param types - an array of <code>JavaClasses</code> for which to generate mappings.
     * @param javaModel - the <code>JavaModel</code> to be used.
     */
    public XJCJavaModelInputImpl(JavaClass[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = types;
    }

    /**
     * Construct a new instance of <code>XJCJavaModelInputImpl</code>.
     *
     * @param types - an array of <code>Types</code> for which to generate mappings.
     * @param javaModel - the <code>JavaModel</code> to be used.
     */
    public XJCJavaModelInputImpl(Type[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = new JavaClass[types.length];
        for (int i = 0; i < types.length; i++) {
            TypeMappingInfo typeMappingInfo = new TypeMappingInfo();
            Type type = types[i];
            typeMappingInfo.setType(type);
            jClasses[i] = jModel.getClass((Class<?>) type);
        }
    }

    /**
     * Construct a new instance of <code>XJCJavaModelInputImpl</code>.
     *
     * @param types - an array of <code>TypeMappingInfos</code> for which to generate mappings.
     * @param javaModel - the <code>JavaModel</code> to be used.
     */
    public XJCJavaModelInputImpl(TypeMappingInfo[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = new JavaClass[types.length];
        for (int i = 0; i < types.length; i++) {
            TypeMappingInfo typeMappingInfo = types[i];
            Type type = typeMappingInfo.getType();
            jClasses[i] = jModel.getClass((Class<?>) type);
        }
    }

    /**
     * Construct a new instance of <code>XJCJavaModelInputImpl</code>.
     *
     * @param types - an array of Java <code>Classes</code> for which to generate mappings.
     * @param javaModel - the <code>JavaModel</code> to be used.
     */
    public XJCJavaModelInputImpl(Class<?>[] classes, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = new JavaClass[classes.length];
        for (int i = 0; i < classes.length; i++) {
            jClasses[i] = jModel.getClass(classes[i]);
        }
    }

    /**
     * Returns this <code>JavaModelInput's</code> array of <code>JavaClasses</code>.
     *
     * @return this <code>JavaModelInput's</code> array of <code>JavaClasses</code>.
     */
    public JavaClass[] getJavaClasses() {
        return jClasses;
    }

    /**
     * Returns this <code>JavaModelInput's</code> <code>JavaModel</code>.
     *
     * @return this <code>JavaModelInput's</code> <code>JavaModel</code>.
     */
    public JavaModel getJavaModel() {
        return jModel;
    }

}
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

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b> <code>JavaModelInput</code> implementation  backed by a collection of MOXY's
 * <code>xmlmodel.JavaClasses</code>..  Used when bootstrapping a <code>DynamicJAXBContext</code> from XML Bindings.
 * </p>
 *
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *    <li>Return an array of <code>JavaClass</code> objects to be used by the generator.</li>
 *    <li>Return the <code>JavaModel</code> to be used during generation.</li>
 * </ul>
 * </p>
 *
 * @since EclipseLink 2.2
 *
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModelInput
 */
public class OXMJavaModelInputImpl implements JavaModelInput {

    private JavaClass[] jClasses;
    private JavaModel jModel;

    /**
     * Construct a new instance of <code>OXMJavaModelInputImpl</code>.
     *
     * @param types - an array of <code>JavaClasses</code> for which to generate mappings.
     * @param javaModel - the <code>JavaModel</code> to be used.
     */
    public OXMJavaModelInputImpl(JavaClass[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = types;
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
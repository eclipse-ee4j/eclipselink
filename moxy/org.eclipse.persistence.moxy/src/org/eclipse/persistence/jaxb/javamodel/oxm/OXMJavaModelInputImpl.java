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
 *     Rick Barkhouse - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.javamodel.oxm;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;

public class OXMJavaModelInputImpl implements JavaModelInput {

    private JavaClass[] jClasses;
    private JavaModel jModel;

    public OXMJavaModelInputImpl(JavaClass[] types, JavaModel javaModel) {
        this.jModel = javaModel;
        this.jClasses = types;
    }

    public JavaClass[] getJavaClasses() {
        return jClasses;
    }

    public JavaModel getJavaModel() {
        return jModel;
    }

}
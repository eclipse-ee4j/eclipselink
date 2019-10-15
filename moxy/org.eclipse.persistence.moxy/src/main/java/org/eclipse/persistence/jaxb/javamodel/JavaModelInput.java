/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.javamodel;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a pluggable method for implementations of the
 * TopLink JAXB 2.0 Java model to be used with the TopLinkJAXB20Generator.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Return an array of JavaClass objects to be used by the generator</li>
 * <li>Return the JavaModel to be used during generation</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaClass
 * @see org.eclipse.persistence.jaxb.javamodel.JavaModel
 */
public interface JavaModelInput {
    public JavaClass[] getJavaClasses();
    public JavaModel getJavaModel();
}

/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

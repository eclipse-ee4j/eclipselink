/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * <p><b>Purpose:</b>A TopLink JAXB 2.0 Java model representation of a JDK Method.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide information about a given implementations underlying method, such
 * as name, parameters, modifiers, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations
 * @see java.lang.reflect.Method
 */
public interface JavaMethod extends JavaHasAnnotations {
    int getModifiers();
    String getName();
    JavaClass getOwningClass();
    JavaClass[] getParameterTypes();
    JavaClass getReturnType();
    boolean isAbstract();
    boolean isFinal();
    boolean isPrivate();
    boolean isProtected();
    boolean isPublic();
    boolean isStatic();
    boolean isSynthetic();
    boolean isBridge();
}

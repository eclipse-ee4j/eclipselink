/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb.javamodel;

import java.util.Collection;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>A superinterface for those interfaces which represent
 * JDK Annotations.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Ensure that subinterfaces define methods for accessing both inherited
 * and direct annotations present on a given JavaAnnotation</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see java.lang.annotation.Annotation
 * @see org.eclipse.persistence.jaxb.javamodel.JavaAnnotation
 */
public interface JavaHasAnnotations {
    public JavaAnnotation getAnnotation(JavaClass arg0);
    public Collection getAnnotations();
    public JavaAnnotation getDeclaredAnnotation(JavaClass arg0);
    public Collection getDeclaredAnnotations();
}

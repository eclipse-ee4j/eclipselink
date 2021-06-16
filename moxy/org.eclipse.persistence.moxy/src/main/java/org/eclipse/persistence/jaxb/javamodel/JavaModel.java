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
 * <p><b>Purpose:</b>The JavaModel is the central access point to the TopLink
 * JAXB 2.0 Java model implementation's source/classes.  A JavaModel has an
 * underlying source/classpath that defines its search path.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Return a JavaClass based on a Class or Class name</li>
 * <li>Return a JDK Annotation for a given JavaAnnotation</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 */
public interface JavaModel {
    public JavaClass getClass(Class<?> jClass);
    public JavaClass getClass(String classname);
    public ClassLoader getClassLoader();
    public java.lang.annotation.Annotation getAnnotation(JavaAnnotation janno, Class<?> jClass);
}

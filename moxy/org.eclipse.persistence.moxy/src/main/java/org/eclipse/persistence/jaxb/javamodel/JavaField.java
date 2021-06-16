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
 * <p><b>Purpose:</b>A TopLink JAXB 2.0 Java model representation of a JDK Field.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide information about a given implementation's underlying field, such
 * as name, type, modifiers, annotations, etc.</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @see org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations
 * @see java.lang.reflect.Field
 */
public interface JavaField extends JavaHasAnnotations {
    public int getModifiers();
    public String getName();
    public JavaClass getResolvedType();
    public boolean isAbstract();
    public boolean isEnumConstant();
    public boolean isFinal();
    public boolean isPrivate();
    public boolean isProtected();
    public boolean isPublic();
    public boolean isStatic();
    public boolean isSynthetic();
}

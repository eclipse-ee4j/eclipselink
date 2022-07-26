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
//     mmacivor - March 12/2009 - Initial implementation

package org.eclipse.persistence.jaxb.javamodel;

/**
 * <b><br>Purpose</b>: JavaModel representation of a java.lang.reflect.Constructor object.
 * <br><b>Reponsibilities:</b>
 * <ul><li>Provide information about a given constructor including such as it's parameter types, access level and modifiers.</li>
 * </ul>
 * @author mmacivor
 *
 */
public interface JavaConstructor {
    int getModifiers();
    String getName();
    JavaClass getOwningClass();
    JavaClass[] getParameterTypes();
    boolean isAbstract();
    boolean isFinal();
    boolean isPrivate();
    boolean isProtected();
    boolean isPublic();
    boolean isStatic();
    boolean isSynthetic();
}

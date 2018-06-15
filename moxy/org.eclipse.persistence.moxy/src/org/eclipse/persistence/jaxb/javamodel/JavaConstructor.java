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
    public int getModifiers();
    public String getName();
    public JavaClass getOwningClass();
    public JavaClass[] getParameterTypes();
    public boolean isAbstract();
    public boolean isFinal();
    public boolean isPrivate();
    public boolean isProtected();
    public boolean isPublic();
    public boolean isStatic();
    public boolean isSynthetic();
}

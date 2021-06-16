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

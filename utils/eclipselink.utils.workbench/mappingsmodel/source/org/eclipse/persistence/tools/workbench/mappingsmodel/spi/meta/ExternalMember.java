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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Interface defining Java metadata required by the
 * TopLink Mapping Workbench.
 *
 * @see java.lang.reflect.Member
 */
public interface ExternalMember {

    /**
     * Returns the ExternalClassDescription object representing
     * the class or interface that declares the member
     * represented by this ExternalMember object.
     *
     * @see java.lang.reflect.Member#getDeclaringClass()
     */
    ExternalClassDescription getDeclaringClass();

    /**
     * Returns the Java language modifiers for the
     * member represented by this ExternalMember object,
     * as an integer.
     *
     * @see java.lang.reflect.Member#getModifiers()
     * @see java.lang.reflect.Modifier
     */
    int getModifiers();

    /**
     * Returns the name of the member represented
     * by this ExternalMember object.
     *
     * @see java.lang.reflect.Member#getName()
     */
    String getName();

    /**
     * Determines if the specified ExternalMember
     * object represents a synthetic member.
     * The TopLink Mapping Workbench will not
     * map synthetic members.
     *
     * See "The Java Virtual Machine Specification" 4.7.6:
     * "A class member that does not appear in the source
     * code must be marked using a Synthetic attribute."
     */
    boolean isSynthetic();

}

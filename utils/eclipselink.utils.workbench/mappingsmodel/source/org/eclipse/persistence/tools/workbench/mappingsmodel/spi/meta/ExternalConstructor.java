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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Interface defining Java metadata required by the
 * TopLink Mapping Workbench.
 *
 * @see java.lang.reflect.Constructor
 */
public interface ExternalConstructor extends ExternalMember {

    /**
     * Returns an array of ExternalClassDescription objects that represent
     * the types of of exceptions declared to be thrown by the
     * underlying constructor represented by this
     * ExternalConstructor object.
     *
     * @see java.lang.reflect.Constructor#getExceptionTypes()
     */
    ExternalClassDescription[] getExceptionTypes();

    /**
     * Returns an array of ExternalClassDescription objects that represent
     * the formal parameter types, in declaration order, of the
     * constructor represented by this ExternalConstructor object.
     *
     * @see java.lang.reflect.Constructor#getParameterTypes()
     */
    ExternalClassDescription[] getParameterTypes();

}

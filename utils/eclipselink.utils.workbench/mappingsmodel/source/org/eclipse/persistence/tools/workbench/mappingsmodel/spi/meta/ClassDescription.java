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
 * This defines a common interface for all the types (internal, external, and user-defined)
 * so they can be consolidated whenever necessary (e.g. in the UI choosers).
 */
public interface ClassDescription {

    /**
     * Returns the name of the entity (class, interface,
     * array class, primitive type, or void) represented by
     * this ClassDescription object, as a String.
     *
     * @see java.lang.Class#getName()
     */
    String getName();

    /**
     * Returns any additional information about the entity
     * (class, interface, array class, primitive type, or void)
     * represented by this ClassDescription object, as a String.
     * This information can be used to differentiate among
     * ClassDescription objects that might have the same name.
     * It can also be used for debugging user-developed
     * external class repositories while using the TopLink
     * Mapping Workbench.
     */
    String getAdditionalInfo();

}

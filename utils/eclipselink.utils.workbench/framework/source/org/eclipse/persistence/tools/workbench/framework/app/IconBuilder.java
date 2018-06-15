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
package org.eclipse.persistence.tools.workbench.framework.app;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;


/**
 * Define an interface that allows the short-circuiting
 * of icon creation by allowing clients to query whether
 * the resulting icon will be the same as an existing one.
 */
public interface IconBuilder {

    /**
     * Build and return icon.
     */
    Icon buildIcon();

    /**
     * Return true if the specified object is another
     * IconBuilder and the icon it will build will have
     * the same appearance as the icon that will be
     * built by this IconBuilder.
     * @see Object#equals(Object)
     */
    boolean equals(Object o);

    /**
     * @see #equals(Object)
     * @see Object#hashCode()
     */
    int hashCode();


    IconBuilder NULL_INSTANCE =
        new IconBuilder() {
            public Icon buildIcon() {
                return EmptyIcon.NULL_INSTANCE;
            }
            public String toString() {
                return "NullIconBuilder";
            }
        };

}

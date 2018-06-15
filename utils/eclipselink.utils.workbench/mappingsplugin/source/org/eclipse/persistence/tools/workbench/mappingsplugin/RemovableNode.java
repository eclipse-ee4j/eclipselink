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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

/**
 * Define an interface that can be used to identify and remove
 * objects from the project tree.
 */
public interface RemovableNode {

    /**
     * Remove the object from the project tree.
     */
    void remove();

    /**
     * Return the object's name, so its removal can be confirmed.
     */
    String getName();

}

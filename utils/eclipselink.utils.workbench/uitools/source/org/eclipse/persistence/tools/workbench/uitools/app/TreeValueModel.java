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
package org.eclipse.persistence.tools.workbench.uitools.app;

/**
 * Extend ValueModel to allow the adding and
 * removing of nodes in a tree value.
 * Typically the value returned from #getValue()
 * will be an Iterator.
 */
public interface TreeValueModel extends ValueModel {

    /**
     * Add the specified node to the tree value.
     */
    void addNode(Object[] parentPath, Object node);

    /**
     * Remove the specified node from the tree value.
     */
    void removeNode(Object[] path);

}

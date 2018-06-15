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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.util.Collection;

/**
 * Interface used by clients to gather up the selected
 * class descriptions from both package and class nodes.
 */
public interface ClassDescriptionNodeContainer {

    /**
     * Add the container's class description nodes to the specified collection.
     */
    void addClassDescriptionNodesTo(Collection classDescriptionNodes);

}

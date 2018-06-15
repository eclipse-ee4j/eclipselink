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

/**
 * This interface is used by a tree cell renderer to delegate any requests
 * for an "accessible" name to the node.
 */
public interface AccessibleNode {

    /**
     * Return a string that can add more description to a rendered node when
     * the text is not sufficient. If null is returned, the text itself is used as the
     * accessible name.
     */
    String accessibleName();

}

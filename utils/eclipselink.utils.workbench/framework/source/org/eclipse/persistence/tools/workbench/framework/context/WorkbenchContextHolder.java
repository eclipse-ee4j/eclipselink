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
package org.eclipse.persistence.tools.workbench.framework.context;

/**
 * Used by the UI to indirectly reference a WorkbenchContext
 * that can change over time (e.g. when a properties page is
 * shared among a set of workbench windows).
 */
public interface WorkbenchContextHolder {

    /**
     * Return the current workbench context.
     */
    WorkbenchContext getWorkbenchContext();

}

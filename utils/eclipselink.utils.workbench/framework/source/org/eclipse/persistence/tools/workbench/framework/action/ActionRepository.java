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
package org.eclipse.persistence.tools.workbench.framework.action;

/**
 * Provided by the WorkbenchContext, there is one action
 * repository per workbench window. The action repository
 * holds the common actions provided by the UI framework
 * (Save, Save-As, Close) that plug-ins might want to
 * include in menus and/or toolbars.
 */
public interface ActionRepository {

    /**
     * Return the framework-supplied Save action.
     */
    FrameworkAction getSaveAction();

    /**
     * Return the framework-supplied Save-As action.
     */
    FrameworkAction getSaveAsAction();

    /**
     * Return the framework-supplied Close action.
     */
    FrameworkAction getCloseAction();

}

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
package org.eclipse.persistence.tools.workbench.framework.context;

import java.awt.Component;
import java.awt.Window;

import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class is used when we only have an application repository.
 * Any call that needs access to "dynamic" state (e.g. the current
 * window) will trigger a runtime exception.
 */
public class ShellWorkbenchContext extends AbstractWorkbenchContext {
    private ApplicationContext applicationContext;
    private static final String CR = StringTools.CR;

    public ShellWorkbenchContext(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public ActionRepository getActionRepository() {
        throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
                "It does not have access to an action repository.");
    }

    public Window getCurrentWindow() {
        throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
                "It does not have access to a current window.");
    }

    public NavigatorSelectionModel getNavigatorSelectionModel() {
        throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
                "It does not have access to a navigator.");
    }

    public Component getPropertiesPage() {
        throw new UnsupportedOperationException("This WorkbenchContext holds only an ApplicationContext." + CR +
        "It does not have access to a properties page.");
    }

}

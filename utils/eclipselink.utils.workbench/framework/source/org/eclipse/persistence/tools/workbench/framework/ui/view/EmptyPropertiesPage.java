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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * NB: Don't make this panel a Singleton or place it in a static field,
 * because you will probably need a separate instance for each open
 * window or view that needs an empty properties page.
 */
public class EmptyPropertiesPage extends AbstractPropertiesPage {

    // this value is queried reflectively during plug-in initialization
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[0];


    public EmptyPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    protected void initializeLayout() {
        // do nothing
    }

}

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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.PluginFactory;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;

/**
 * straightforward singleton implementation
 */
public final class PlatformsPluginFactory
    implements PluginFactory
{
    // singleton
    private static PluginFactory INSTANCE;


    /**
     * Return the singleton.
     */
    public static synchronized PluginFactory instance() {
        if (INSTANCE == null) {
            INSTANCE = new PlatformsPluginFactory();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private PlatformsPluginFactory() {
        super();
    }


    // ********** PluginFactory implementation **********

    public Plugin createPlugin(ApplicationContext context) {
        return new PlatformsPlugin();
    }

}

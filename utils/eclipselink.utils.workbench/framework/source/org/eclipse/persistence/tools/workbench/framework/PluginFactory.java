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
package org.eclipse.persistence.tools.workbench.framework;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;

/**
 * Implementations of this interface are the root objects for clients of
 * the UI framework.
 */
public interface PluginFactory {

    /**
     * Build and return a plug-in to be "plugged in" to the UI framework.
     */
    Plugin createPlugin(ApplicationContext context);

}

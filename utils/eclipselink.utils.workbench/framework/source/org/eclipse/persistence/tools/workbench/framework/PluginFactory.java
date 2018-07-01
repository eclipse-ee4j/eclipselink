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

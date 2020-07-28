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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.awt.Component;

/**
 * An implementing class should describe how to build a <code>Component</code>
 * based upon the given actions described in the <code>ActionContainer</code>
 * interface.
 *
 * @see org.eclipse.persistence.tools.workbench.framework.app.ActionContainer
 * @see java.awt.Component
 * @version 10.1.3
 */
public interface ComponentDescription extends ActionContainer
{
    /**
     * Returns a <code>Component</code> built based upon the description
     * of the implementing class.
     */
    public Component component();
}

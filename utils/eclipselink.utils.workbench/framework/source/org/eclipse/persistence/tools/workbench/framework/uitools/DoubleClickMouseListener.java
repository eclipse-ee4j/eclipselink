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
package org.eclipse.persistence.tools.workbench.framework.uitools;

// JDK
import java.awt.event.MouseEvent;
import java.util.EventListener;

/**
 * This listener is notified when a double click has been done on a component.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public interface DoubleClickMouseListener extends EventListener
{
    /**
     * Invoked when the mouse button has been double clicked on a component.
     *
     * @param e The <code>MouseEvent</code>
     */
    public void mouseDoubleClicked(MouseEvent e);
}

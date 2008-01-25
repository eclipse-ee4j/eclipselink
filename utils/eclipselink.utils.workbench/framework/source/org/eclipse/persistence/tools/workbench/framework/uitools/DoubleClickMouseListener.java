/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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
/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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

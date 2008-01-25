/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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

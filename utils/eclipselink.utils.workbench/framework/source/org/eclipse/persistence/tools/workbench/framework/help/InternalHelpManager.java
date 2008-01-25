/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.help;

/**
 * Used by HelpFacade internally.
 */
interface InternalHelpManager extends HelpManager {

	/**
	 * Set whether we had problems loading the "local" help book.
	 */
	void setLocalHelpFailed(boolean localHelpFailed);
	 
	/**
	 * The host application has finished its start-up;
	 * the Help system can now interact with the user.
	 */
	void launchComplete();
	 
}

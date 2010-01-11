/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.app;

import java.awt.Component;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * Define the node methods needed by an editor view.
 */
public interface EditorNode {

	/**
	 * Return a properties page populated with the node's "user object"
	 * This page will be displayed in an editor view.
	 */
	Component propertiesPage(WorkbenchContext workbenchContext);

	/**
	 * Return an icon that can be used in the properties page title bar.
	 * When the icon changes, the model should fire
	 * the appropriate change notification:
	 *     this.firePropertyChanged(PROPERTIES_PAGE_TITLE_ICON_PROPERTY, oldPropertiesPageTitleIcon, this.propertiesPageTitleIcon());
	 */
	Icon propertiesPageTitleIcon();
		String PROPERTIES_PAGE_TITLE_ICON_PROPERTY = "propertiesPageTitleIcon";

	/**
	 * Return a string that can be used in the properties page title bar.
	 * When the title changes, the model should fire
	 * the appropriate change notification:
	 *     this.firePropertyChanged(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY, oldPropertiesPageTitleText, this.propertiesPageTitleText());
	 */
	String propertiesPageTitleText();
		String PROPERTIES_PAGE_TITLE_TEXT_PROPERTY = "propertiesPageTitleText";

	/**
	 * Release the previously-provided properties page;
	 * it is no longer needed by the editor view.
	 */
	void releasePropertiesPage(Component propertiesPage);

}

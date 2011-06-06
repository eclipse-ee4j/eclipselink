/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;


/**
 * We subclass DefaultMutableTreeNode because preferences nodes are
 * static. We cache the nodes label text and properties page.
 */
public abstract class AbstractPreferencesNode
	extends DefaultMutableTreeNode
	implements PreferencesNode
{
	protected String displayString;
	protected Component propertiesPage;


	// ********** constructors/initialization **********

	/**
	 * Construct a node with the specified context.
	 * Subclasses can wrap the context in their constructors
	 * before calling super(PreferencesContext).
	 */
	protected AbstractPreferencesNode(PreferencesContext context) {
		// store the context in the node's "user object"
		super(context);
		this.initialize();
	}

	protected void initialize() {
		// allow subclasses to override and add sub-nodes etc.
	}


	// ********** EditorNode implementation **********

	/**
	 * postpone building the properties page until it is first needed,
	 * then cache it; this also allows us to have any extra state in
	 * place before building the page
	 * @see EditorNode#propertiesPage(WorkbenchContext)
	 */
	public Component propertiesPage(WorkbenchContext workbenchContext) {
		if (this.propertiesPage == null) {
			this.propertiesPage = this.buildPropertiesPage();
		}
		return this.propertiesPage;
	}

	/**
	 * This method will only be called if the cached properties
	 * page is missing.
	 */
	protected abstract Component buildPropertiesPage();

	/**
	 * @see EditorNode#propertiesPageIcon()
	 */
	public Icon propertiesPageTitleIcon() {
		return null;
	}

	/**
	 * @see EditorNode#propertiesPageTitle()
	 */
	public String propertiesPageTitleText() {
		return this.displayString();
	}

	/**
	 * since there is only one preferences view, we do not need
	 * to share the properties page
	 * @see EditorNode#releasePropertiesPage(java.awt.Component)
	 */
	public void releasePropertiesPage(Component page) {
		// do nothing
	}


	// ********** PreferencesNode implementation **********

	/**
	 * @see PreferencesNode#getPreferences()
	 */
	public Preferences getPreferences() {
		return this.getPreferencesContext().getPreferences();
	}

	/**
	 * postpone building the display string until it is first needed,
	 * then cache it; this also allows us to have any extra state in
	 * place before building the string
	 * @see PreferencesNode#displayString()
	 */
	public String displayString() {
		if (this.displayString == null) {
			this.displayString = this.buildDisplayString();
		}
		return this.displayString;
	}

	/**
	 * This method will only be called if the cached display
	 * string is missing.
	 */
	protected abstract String buildDisplayString();

	/**
	 * @see PreferencesNode#helpTopicId()
	 */
	public String helpTopicId() {
		return null;
	}


	// ********** queries **********

	/**
	 * convenience method - cast the node's "user object"
	 */
	protected PreferencesContext getPreferencesContext() {
		return (PreferencesContext) this.getUserObject();
	}

	protected Application application() {
		return this.getPreferencesContext().getApplication();
	}

	protected Preferences preferences() {
		return this.getPreferencesContext().getPreferences();
	}

	public ResourceRepository resourceRepository() {
		return this.getPreferencesContext().getResourceRepository();
	}

	protected HelpManager helpManager() {
		return this.getPreferencesContext().getHelpManager();
	}
	
	public void showHelp() {
		this.helpManager().showTopic(this.helpTopicId()); 
	}
}

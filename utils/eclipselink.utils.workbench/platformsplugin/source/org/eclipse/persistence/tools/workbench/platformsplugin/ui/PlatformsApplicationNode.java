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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.utility.node.NodeModel;


/**
 * add support for a properties page cache and some convenience methods
 */
public abstract class PlatformsApplicationNode
	extends AbstractApplicationNode
{

	// ********** constructor **********

	public PlatformsApplicationNode(NodeModel value, TreeNodeValueModel parent, Plugin plugin, ApplicationContext context) {
		super(value, parent, plugin, context);
	}


	// ********** EditorNode implementation **********

	/**
	 * The default implementation caches properties pages in the
	 * platforms plug-in. The cache key is determined by subclasses.
	 */
	public Component propertiesPage(WorkbenchContext context) {
		WorkbenchContext localContext = this.buildLocalWorkbenchContext(context);
		AbstractPropertiesPage propertiesPage = (AbstractPropertiesPage) this.getPlatformsPlugin().getPropertiesPage(this.propertiesPageClass());
		if (propertiesPage == null) {
			propertiesPage = this.buildPropertiesPage(localContext);
		}
		propertiesPage.setNode(this, localContext);
		return propertiesPage;
	}

	/**
	 * Return the key used to cache the properties page; typically
	 * this is the class of the properties page.
	 * This method need not be overridden if the methods
	 * #propertiesPage(WorkbenchContext) and #releasePropertiesPage(Component)
	 * are overridden with different behavior.
	 */
	protected Class propertiesPageClass() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Build and return a properties page.
	 * This method need not be overridden if the methods
	 * #propertiesPage(WorkbenchContext) and #releasePropertiesPage(Component)
	 * are overridden with different behavior.
	 */
	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * The default implementation caches properties pages in the
	 * platforms plug-in. The cache key is determined by subclasses.
	 */
	public void releasePropertiesPage(Component propertiesPage) {
		this.releasePropertiesPage((AbstractPropertiesPage) propertiesPage);
	}

	/**
	 * The default implementation caches properties pages in the
	 * platforms plug-in. The cache key is determined by subclasses.
	 */
	protected void releasePropertiesPage(AbstractPropertiesPage propertiesPage) {
		// clear out the node so it can be garbage-collected if necessary and
		// replace the workbench context with a shell
		propertiesPage.setNode(null, this.buildShellWorkbenchContext());
		this.getPlatformsPlugin().releasePropertiesPage(this.propertiesPageClass(), propertiesPage);
	}


	// ********** convenience methods **********

	/**
	 * this method name can be changed to getProjectRoot() when we move to jdk1.5
	 */
	public DatabasePlatformRepositoryNode getRepositoryNode() {
		return (DatabasePlatformRepositoryNode) this.getProjectRoot();
	}

	/**
	 * this method name can be changed to getPlugin() when we move to jdk1.5
	 */
	protected PlatformsPlugin getPlatformsPlugin() {
		return (PlatformsPlugin) this.getPlugin();
	}

	protected MenuGroupDescription buildHelpMenuGroup(WorkbenchContext context) {
		MenuGroupDescription helpGroup = new MenuGroupDescription();
		helpGroup.add(this.getPlatformsPlugin().getHelpAction(context));
		return helpGroup;
	}

}

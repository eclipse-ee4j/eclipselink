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
package org.eclipse.persistence.tools.workbench.scplugin.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * Base class for all SC Nodes.
 */
public abstract class SCApplicationNode extends AbstractApplicationNode {

	// ********** constructors/initialization **********
	
	public SCApplicationNode( SCAdapter scAdapter, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {
	
		super( scAdapter, parent, plugin, context);
	}
	
	// ********** EditorNode implementation **********

	/**
	 * The default implementation caches properties pages in the
	 * MW plug-in. The cache key is determined by subclasses.
	 */
	public Component propertiesPage(WorkbenchContext context) {
		WorkbenchContext localContext = this.buildLocalWorkbenchContext(context);
		AbstractPropertiesPage propertiesPage = (AbstractPropertiesPage) this.getSCPlugin().getPropertiesPage(this.propertiesPageKey());
		if (propertiesPage == null) {
			propertiesPage = this.buildPropertiesPage(localContext);
		}
		propertiesPage.setNode(this, localContext);
		return propertiesPage;
	}


	/**
	 * Return the key used to cache the properties page; typically
	 * this is the class of the properties page. This method need
	 * not be implemented if the methods #propertiesPage() and
	 * #releasePropertiesPage(AbstractPropertiesPage) are overridden
	 * with different behavior.
	 */
	protected Object propertiesPageKey() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Build and return a properties page with the node already installed.
	 * This method need not be implemented if the methods #propertiesPage()
	 * and #releasePropertiesPage(AbstractPropertiesPage) are overridden
	 * with different behavior.
	 */
	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * The default implementation caches properties pages in the
	 * MW plug-in. The cache key is determined by subclasses.
	 */
	public void releasePropertiesPage(Component propertiesPage) {
		this.releasePropertiesPage((AbstractPropertiesPage) propertiesPage);
	}

	/**
	 * The default implementation caches properties pages in the
	 * MW plug-in. The cache key is determined by subclasses.
	 */
	protected void releasePropertiesPage(AbstractPropertiesPage propertiesPage) {
		// clear out the node so it can be garbage-collected if necessary
		propertiesPage.setNode(null, this.buildShellWorkbenchContext());
		this.getSCPlugin().releasePropertiesPage(this.propertiesPageKey(), propertiesPage);
	}

	protected SCPlugin getSCPlugin() {
		return ( SCPlugin)this.getPlugin();
	}
	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);

		RootMenuDescription desc = new RootMenuDescription();
		MenuGroupDescription deleteRenameGroup = new MenuGroupDescription();
		deleteRenameGroup.add(buildRenameNodeAction(wrappedContext));
		deleteRenameGroup.add(buildDeleteNodeAction(wrappedContext));
		desc.add(deleteRenameGroup);
		
		desc.add(buildHelpMenuGroup(wrappedContext));
		
		return desc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);

		ToolBarDescription desc = new ToolBarDescription();
		ToolBarButtonGroupDescription deleteRenameGroup = new ToolBarButtonGroupDescription();
		deleteRenameGroup.add(buildRenameNodeAction(wrappedContext));
		deleteRenameGroup.add(buildDeleteNodeAction(wrappedContext));
		desc.add(deleteRenameGroup);
		
		return desc;
	}
	
	protected List buildDisplayStringPropertyNamesList() {
		
		ArrayList displayStrings = new ArrayList();
		return displayStrings;
	}
	
	abstract protected FrameworkAction buildRenameNodeAction(WorkbenchContext workbenchContext);
	abstract protected FrameworkAction buildDeleteNodeAction(WorkbenchContext workbenchContext);
	
	protected FrameworkAction getRenameNodeAction(WorkbenchContext workbenchContext) {
		return buildRenameNodeAction(workbenchContext);
	}
	
	protected FrameworkAction getDeleteNodeAction(WorkbenchContext workbenchContext) {
		return buildDeleteNodeAction(workbenchContext);
	}
	
	protected MenuGroupDescription buildHelpMenuGroup(WorkbenchContext workbenchContext)
	{
		MenuGroupDescription desc = new MenuGroupDescription();
		desc.add(getHelpAction(workbenchContext));
		return desc;
	}
	
	protected FrameworkAction getHelpAction(WorkbenchContext workbenchContext)
	{
		return new OracleHelpAction(workbenchContext);
	}

	protected String[] displayStringPropertyNames() {

		List actions = this.buildDisplayStringPropertyNamesList();

		return ( String[])actions.toArray( new String[ actions.size()]);
	}
}

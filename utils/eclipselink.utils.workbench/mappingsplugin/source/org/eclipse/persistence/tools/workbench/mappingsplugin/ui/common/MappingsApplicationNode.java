/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common;

import java.awt.Component;
import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * Add a properties page cache and some convenience methods.
 */
public abstract class MappingsApplicationNode
	extends AbstractApplicationNode
{

	// ********** constructor **********

	public MappingsApplicationNode(MWModel model, TreeNodeValueModel parent, Plugin plugin, ApplicationContext context) {
		super(model, parent, plugin, context);
	}


	// ********** EditorNode implementation **********

	/**
	 * The default implementation caches properties pages in the
	 * MW plug-in.
	 */
	public Component propertiesPage(WorkbenchContext context) {
		AbstractPropertiesPage propertiesPage = (AbstractPropertiesPage) this.getMappingsPlugin().buildPropertiesPage(this.propertiesPageClass());
		propertiesPage.setNode(this, this.buildLocalWorkbenchContext(context));
		return propertiesPage;
	}

	/**
	 * Return the class of the node's properties page.
	 * This method need not be overridden if the methods
	 * #propertiesPage(WorkbenchContext) and #releasePropertiesPage(Component)
	 * are overridden with different behavior that does not use the plug-in cache.
	 */
	protected Class propertiesPageClass() {
		throw new UnsupportedOperationException();
	}

	/**
	 * The default implementation caches properties pages in the
	 * MW plug-in.
	 */
	public void releasePropertiesPage(Component propertiesPage) {
		this.releasePropertiesPage((AbstractPropertiesPage) propertiesPage);
	}

	/**
	 * The default implementation caches properties pages in the
	 * MW plug-in.
	 */
	protected void releasePropertiesPage(AbstractPropertiesPage propertiesPage) {
		// clear out the node so it can be garbage-collected if necessary and
		// replace the workbench context with a shell
		propertiesPage.setNode(null, this.buildShellWorkbenchContext());
		this.getMappingsPlugin().releasePropertiesPage(propertiesPage);
	}


	// ********** public API **********

	/**
	 * Return a candidate package name for a new class/descriptor.
	 * The default is no name.
	 */
	public String candidatePackageName() {
		return "";
	}

	/**
	 * Return whether the node is auto-mappable.
	 */
	public boolean isAutoMappable() {
		return false;
	}

	/**
	 * Add the descriptors associated with the node to
	 * the specified collection.
	 */
	public void addDescriptorsTo(Collection descriptors) {
		// do nothing by default
	}

	
	// ********** convenience methods **********

	/**
	 * this method name can be changed to getProjectRoot() when we move to jdk1.5
	 */
	public ProjectNode getProjectNode() {
		return (ProjectNode) this.getProjectRoot();
	}

	/**
	 * this method name can be changed to getValue() when we move to jdk1.5
	 */
	protected MWModel getMWModelValue() {
		return (MWModel) this.getValue();
	}

	/**
	 * this method name can be changed to getPlugin() when we move to jdk1.5
	 */
	protected MappingsPlugin getMappingsPlugin() {
		return (MappingsPlugin) this.getPlugin();
	}

	protected MenuGroupDescription buildOracleHelpMenuGroup(WorkbenchContext context) {
		MenuGroupDescription helpGroup = new MenuGroupDescription();
		helpGroup.add(this.getMappingsPlugin().getHelpAction(context));
		return helpGroup;
	}

}

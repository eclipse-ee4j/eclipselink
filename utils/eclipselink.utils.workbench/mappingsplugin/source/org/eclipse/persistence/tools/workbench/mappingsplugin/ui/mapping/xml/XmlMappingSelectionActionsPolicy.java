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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItemDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButtonDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MapAsTransformationAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingSelectionActionsPolicy;


public abstract class XmlMappingSelectionActionsPolicy 
	extends MappingSelectionActionsPolicy 
{
	// **************** Constructors ******************************************

	protected XmlMappingSelectionActionsPolicy(MappingsPlugin plugin) {
		super(plugin);
	}
	
	
	// **************** Mapping toolbar ***************************************
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext context) {
		GroupContainerDescription toolBarDescription = new ToolBarDescription();
		toolBarDescription.add(buildMapAsToolBarGroup(context));
		
		return toolBarDescription;
	}
	
	protected abstract ToolBarButtonGroupDescription buildMapAsToolBarGroup(WorkbenchContext context);
	
	protected void addDirectMappingButtons(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
		toolBarGroup.add(buildXmlDirectToolBarButton(context));
		toolBarGroup.add(buildXmlDirectCollectionToolBarButton(context));
	}
	
	protected final ToggleToolBarButtonDescription buildXmlDirectToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(getMapAsXmlDirectAction(context));
	}
	
	protected final ToggleToolBarButtonDescription buildXmlDirectCollectionToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(getMapAsXmlDirectCollectionAction(context));
	}
	
	protected void addCompositeMappingButtons(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
		toolBarGroup.add(buildCompositeObjectToolBarButton(context));
		toolBarGroup.add(buildCompositeCollectionToolBarButton(context));
	}
	
	protected final ToggleToolBarButtonDescription buildCompositeCollectionToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(getMapAsCompositeCollectionAction(context));
	}
	
	protected final ToggleToolBarButtonDescription buildCompositeObjectToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(getMapAsCompositeObjectAction(context));
	}
	
	protected void addTransformationMappingButton(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
		toolBarGroup.add(buildTransformationToolbarButton(context));
	}
	
	protected final ToggleToolBarButtonDescription buildTransformationToolbarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(getMapAsTransformationAction(context));
	}
	
	protected void addUnmapButton(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
		toolBarGroup.add(this.buildUnmappedToolBarButton(context));
	}
	
	
	// **************** Mapping menu ******************************************
	
	protected void addToMapAsMenuDescription(MenuDescription menuDescription, WorkbenchContext context) {
		menuDescription.add(buildMapAsMenuGroup(context));
	}

	protected abstract MenuGroupDescription buildMapAsMenuGroup(WorkbenchContext context);
	
	protected void addDirectMappingMenuItems(MenuGroupDescription menuGroup, WorkbenchContext context) {
		menuGroup.add(this.buildXmlDirectMenuItem(context));
		menuGroup.add(this.buildXmlDirectCollectionMenuItem(context));
	}
	
	private final ToggleMenuItemDescription buildXmlDirectMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(getMapAsXmlDirectAction(context));
	}
	
	private final ToggleMenuItemDescription buildXmlDirectCollectionMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(getMapAsXmlDirectCollectionAction(context));
	}
	
	protected void addCompositeMappingMenuItems(MenuGroupDescription menuGroup, WorkbenchContext context) {
		menuGroup.add(this.buildCompositeObjectMenuItem(context));
		menuGroup.add(this.buildCompositeCollectionMenuItem(context));
	}
	
	private final ToggleMenuItemDescription buildCompositeObjectMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(getMapAsCompositeObjectAction(context));
	}
	
	private final ToggleMenuItemDescription buildCompositeCollectionMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(getMapAsCompositeCollectionAction(context));
	}
	
	protected void addTransformationMappingMenuItem(MenuGroupDescription menuGroup, WorkbenchContext context) {
		menuGroup.add(this.buildTransformationMenuItem(context));
	}
	
	private final ToggleMenuItemDescription buildTransformationMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(getMapAsTransformationAction(context));
	}
	
	protected void addUnmapMenuItem(MenuGroupDescription menuGroup, WorkbenchContext context) {
		menuGroup.add(this.buildUnmappedMenuItem(context));
	}
	
	
	// **************** Mapping actions ***************************************
	
	protected abstract ToggleFrameworkAction getMapAsXmlDirectAction(WorkbenchContext context);
	
	protected abstract ToggleFrameworkAction getMapAsXmlDirectCollectionAction(WorkbenchContext context);
	
	private ToggleFrameworkAction getMapAsCompositeObjectAction(WorkbenchContext context) {
		return new MapAsCompositeObjectAction(context);
	}
	
	private ToggleFrameworkAction getMapAsCompositeCollectionAction(WorkbenchContext context) {
		return new MapAsCompositeCollectionAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsTransformationAction(WorkbenchContext context) {
		return new MapAsTransformationAction(context);
	}
}

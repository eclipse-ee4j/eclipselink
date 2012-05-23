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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItemDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButtonDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public abstract class MappingSelectionActionsPolicy
	implements SelectionActionsPolicy 
{
	protected static final Icon EMPTY_ICON = new EmptyIcon(16);

	
	// **************** Variables *********************************************
	
	private MappingsPlugin mwPlugin;
		
	// **************** Constructors ******************************************
	
	public MappingSelectionActionsPolicy(MappingsPlugin plugin) {
		super();
		this.mwPlugin = plugin;
	}
	
	
	// **************** Plugin ************************************************
	
	protected MappingsPlugin getMappingsPlugin() {
		return this.mwPlugin;
	}
	
	
	// **************** SelectionActionsPolicy contract ***********************
	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		RootMenuDescription menuDesc = new RootMenuDescription();
		
		MenuGroupDescription classActionGroup = new MenuGroupDescription();
		classActionGroup.add(this.mwPlugin.getRefreshClassesAction(context));
		classActionGroup.add(this.mwPlugin.getAddOrRefreshClassesAction(context));
		classActionGroup.add(this.mwPlugin.getCreateNewClassAction(context));
		menuDesc.add(classActionGroup);
		
		MenuGroupDescription removeGroup = new MenuGroupDescription();
		removeGroup.add(getRemoveAction(context));
		menuDesc.add(removeGroup);
		
		MenuGroupDescription mapAsGroup = new MenuGroupDescription();
		mapAsGroup.add(buildMapAsMenuDescription(context));
		menuDesc.add(mapAsGroup);
		
		return menuDesc;
	}
	
	public final MenuDescription buildMapAsMenuDescription(WorkbenchContext context) 
	{
		MenuDescription desc = new MenuDescription(context.getApplicationContext().getResourceRepository().getString("CHANGE_MAPPING_TYPE_MENU"),
				context.getApplicationContext().getResourceRepository().getString("CHANGE_MAPPING_TYPE_MENU"), 
				context.getApplicationContext().getResourceRepository().getMnemonic("CHANGE_MAPPING_TYPE_MENU"),
				EMPTY_ICON
		);
		addToMapAsMenuDescription(desc, context);
		return desc;
	}
	
	protected abstract void addToMapAsMenuDescription(MenuDescription menu, WorkbenchContext context);
	
	
	
	protected ToggleFrameworkAction getMapAsUnmappedAction(WorkbenchContext context) {
		return new MapAsUnmappedAction(context);
	}

	protected final ToggleMenuItemDescription buildUnmappedMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(getMapAsUnmappedAction(context));
	}

	protected final ToggleToolBarButtonDescription buildUnmappedToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(getMapAsUnmappedAction(context));
	}

	
	private FrameworkAction getRemoveAction(WorkbenchContext context) {
		return new RemoveAction(context);
	}


	// ********** inner class **********

	private static class RemoveAction extends AbstractEnablableFrameworkAction {

		RemoveAction(WorkbenchContext context) {
			super(context);		
		}

		protected void initialize() {
			super.initialize();
			this.initializeIcon("remove");
			this.initializeTextAndMnemonic("REMOVE_ACTION");
			this.initializeToolTipText("REMOVE_ACTION.toolTipText");
		}

		protected void execute() {
			if ( ! this.confirmRemoval()) {
				return;
			}
			ApplicationNode[] selectedNodes = this.selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++) {
				((MappingNode) selectedNodes[i]).remove();
			}	
		}

		private boolean confirmRemoval() {
			return JOptionPane.YES_OPTION ==
			JOptionPane.showConfirmDialog(
					this.getWorkbenchContext().getCurrentWindow(),
					new LabelArea(this.resourceRepository().getString("REMOVE_MAPPING_ACTION_DIALOG.message", StringTools.CR)),
					this.resourceRepository().getString("REMOVE_MAPPING_ACTION_DIALOG.title"),
					JOptionPane.YES_NO_OPTION
			);
		}
		
		protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
			return true;
		}

	}

}

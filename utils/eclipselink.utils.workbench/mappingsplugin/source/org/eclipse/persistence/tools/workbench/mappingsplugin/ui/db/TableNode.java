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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RemovableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RemoveAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;


public final class TableNode extends MappingsApplicationNode 
	implements RemovableNode {
			
	protected static final String[] TABLE_DISPLAY_STRING_PROPERTY_NAMES = {
			MWTable.QUALIFIED_NAME_PROPERTY
	};

	
	public TableNode(MWTable table, DatabaseNode parent) {
		super(table, parent, parent.getPlugin(), parent.getApplicationContext());
	}
	
	
	// ********** ApplicationNode implementation **********

	public String helpTopicID() {
		return "table";
	}
		

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return TableTabbedPropertiesPage.class;
	}


	// ********** AbstractApplicationNode overrides **********

	public String buildIconKey() {
		return "table";
	}
	
	protected String[] displayStringPropertyNames() {
		return TABLE_DISPLAY_STRING_PROPERTY_NAMES;
	}

	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		WorkbenchContext localContext = this.buildLocalWorkbenchContext(workbenchContext);
		
		RootMenuDescription desc = new RootMenuDescription();
		MenuGroupDescription refreshGroup = new MenuGroupDescription();
		refreshGroup.add(this.getRefreshAction(localContext));
		desc.add(refreshGroup);
		
		MenuGroupDescription removeRenameGroup = new MenuGroupDescription();
		removeRenameGroup.add(this.getRemoveAction(localContext));
		removeRenameGroup.add(this.getRenameAction(localContext));
		desc.add(removeRenameGroup);
		
		MenuGroupDescription subMenusGroup = new MenuGroupDescription();
		subMenusGroup.add(this.buildGenerateCreationScriptMenuDescription(localContext));
		subMenusGroup.add(this.buildCreateOnDatabaseMenuDescription(localContext));
		subMenusGroup.add(this.buildGenerateDescriptorFromTablesMenu(localContext));
		desc.add(subMenusGroup);
		
		desc.add(this.buildOracleHelpMenuGroup(localContext));
		
		return desc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		WorkbenchContext localContext = this.buildLocalWorkbenchContext(workbenchContext);

		ToolBarDescription desc = new ToolBarDescription();
		ToolBarButtonGroupDescription refreshGroup = new ToolBarButtonGroupDescription();
		refreshGroup.add(this.getRefreshAction(localContext));
		desc.add(refreshGroup);
		
		ToolBarButtonGroupDescription removeRenameGroup = new ToolBarButtonGroupDescription();
		removeRenameGroup.add(this.getRemoveAction(localContext));
		removeRenameGroup.add(this.getRenameAction(localContext));
		desc.add(removeRenameGroup);
		
		return desc;
	}

	private MenuDescription buildGenerateCreationScriptMenuDescription(WorkbenchContext workbenchContext) {
		MenuDescription desc = new MenuDescription(this.resourceRepository().getString("GENERATE_CREATION_SCRIPT_MENU"),
														this.resourceRepository().getString("GENERATE_CREATION_SCRIPT_MENU.toolTipText"),
														this.resourceRepository().getMnemonic("GENERATE_CREATION_SCRIPT_MENU"),
														EMPTY_ICON);
		MenuGroupDescription generateGroup = new MenuGroupDescription();
		
		generateGroup.add(this.getGenerateCreationScriptForAllTablesAction(workbenchContext));
		generateGroup.add(this.getGenerateCreationScriptForSelectedTablesAction(workbenchContext));
		desc.add(generateGroup);
		
		return desc;
	}
	
	private MenuDescription buildCreateOnDatabaseMenuDescription(WorkbenchContext workbenchContext) {
		MenuDescription desc = new MenuDescription(this.resourceRepository().getString("CREATE_ON_DATABASE_MENU"),
											this.resourceRepository().getString("CREATE_ON_DATABASE_MENU.toolTipText"),
											this.resourceRepository().getMnemonic("CREATE_ON_DATABASE_MENU"),
											EMPTY_ICON);
		MenuGroupDescription createGroup = new MenuGroupDescription();
		createGroup.add(this.getCreateOnDatabaseForAllTablesAction(workbenchContext));
		createGroup.add(this.getCreateOnDatabaseForSelectedTablesAction(workbenchContext));
		desc.add(createGroup);
		
		return desc;
	}
	
	private MenuDescription buildGenerateDescriptorFromTablesMenu(WorkbenchContext workbenchContext) {
		MenuDescription desc = new MenuDescription(this.resourceRepository().getString("GENERATE_DESCRIPTORS_FROM_TABLES_ACTION"),
												this.resourceRepository().getString("GENERATE_DESCRIPTORS_FROM_TABLES_ACTION.toolTipText"),
												this.resourceRepository().getMnemonic("GENERATE_DESCRIPTORS_FROM_TABLES_ACTION"),
												EMPTY_ICON);
		MenuGroupDescription generateGroup = new MenuGroupDescription();		
		generateGroup.add(this.getGenerateDescriptorsFromAllTablesAction(workbenchContext));
		generateGroup.add(this.getGenerateDescriptorsFromSelectedTablesAction(workbenchContext));
		desc.add(generateGroup);
		
		return desc;
	}		
	
	// ********** Removable iplementation **********
	
	public String getName() {
		return getTable().getName();
	}
	
	public void remove() {
		getTable().getDatabase().removeTable(getTable());
	}

	
	// ********** convenience methods **********

	public MWTable getTable() {
		return (MWTable) this.getValue();
	}
	

	// ********** actions **********

	private FrameworkAction getRefreshAction(WorkbenchContext context) {
		return new RefreshTableAction(context);
	}
	
	private FrameworkAction getRemoveAction(WorkbenchContext context) {
		return new RemoveAction(context, "table.remove");
	}
	
	private FrameworkAction getRenameAction(WorkbenchContext context) {
		return new RenameTableAction(context);
	}
	
	private FrameworkAction getGenerateCreationScriptForSelectedTablesAction(WorkbenchContext context) {
		return new GenerateCreationScriptForSelectedTablesAction(context);
	}
	
	private FrameworkAction getGenerateCreationScriptForAllTablesAction(WorkbenchContext context) {
		return new GenerateCreationScriptForAllTablesAction(context);
	}
	
	private FrameworkAction getCreateOnDatabaseForSelectedTablesAction(WorkbenchContext context) {
		return new CreateSelectedTablesOnDatabaseAction(context);
	}
	
	private FrameworkAction getCreateOnDatabaseForAllTablesAction(WorkbenchContext context) {
		return new CreateAllTablesOnDatabaseAction(context);
	}

	private FrameworkAction getGenerateDescriptorsFromAllTablesAction(WorkbenchContext context) {
		return new GenerateDescriptorsFromAllTablesAction(context);
	}
	
	private FrameworkAction getGenerateDescriptorsFromSelectedTablesAction(WorkbenchContext context) {
		return new GenerateDescriptorsFromSelectedTablesAction(context);
	}
	
	// *********** behavior ***************
    
    public void selectColumn(MWColumn column, WorkbenchContext context) {
        ((TableTabbedPropertiesPage) context.getPropertiesPage()).selectColumn(column);
    }
    
    public void selectReference(MWReference reference, WorkbenchContext context) {
        ((TableTabbedPropertiesPage) context.getPropertiesPage()).selectReference(reference);
    }   

}

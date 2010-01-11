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

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public final class DatabaseNode
	extends MappingsApplicationNode
	implements ProjectNode.Child
{

	private ListValueModel childrenModel;

	protected static final String[] DATABASE_DISPLAY_STRING_PROPERTY_NAMES = {
			MWDatabase.DATABASE_PLATFORM_PROPERTY
	};
	protected static final String[] DATABASE_ICON_PROPERTY_NAMES = {
			Node.HAS_BRANCH_PROBLEMS_PROPERTY,
			MWDatabase.CONNECTED_PROPERTY
	};


	// ********** constructors/initialization **********

	public DatabaseNode(MWDatabase database, RelationalProjectNode parent, MappingsPlugin plugin, ApplicationContext context) {
		super(database, parent, plugin, context);
	}

	protected void initialize() {
		super.initialize();
		this.childrenModel = this.buildChildrenModel();
	}

	// the list should be sorted
	protected ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter());
	}

	// the display string (name) of each table node can change
	protected ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildDatabaseNodeAdapter(), DISPLAY_STRING_PROPERTY);
	}

	// wrap the tables in table nodes
	protected ListValueModel buildDatabaseNodeAdapter() {
		return new TransformationListValueModelAdapter(this.buildTablesAspectAdapter()) {
			protected Object transformItem(Object item) {
				return new TableNode((MWTable) item, DatabaseNode.this);
			}
		};
	}

	// the list of tables can change
	protected CollectionValueModel buildTablesAspectAdapter() {
		return new CollectionAspectAdapter(this, MWDatabase.TABLES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWDatabase) this.subject).tables();
			}
			protected int sizeFromSubject() {
				return ((MWDatabase) this.subject).tablesSize();
			}
		};
	}	



	// *********** ProjectNode.Child implementation *********

	public int getProjectNodeChildPriority() {
		return 1;
	}


	// ********** ApplicationNode implementation **********

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	public String helpTopicID() {
		return "database";
	}

	public String buildIconKey() {
		return this.getDatabase().isConnected() ? "database.connected" : "database";
	}
	
	private FrameworkAction getAddNewTableAction(WorkbenchContext context) {
		return new AddNewTableAction(context);
	}
	
	private FrameworkAction getAddOrRefreshTablesAction(WorkbenchContext context) {
		return new AddOrRefreshTablesAction(context);
	}
	
	private FrameworkAction getDatabaseLogInAction(WorkbenchContext context) {
		return new LogInAction(context);
	}
	
	private FrameworkAction getDatabaseLogOutAction(WorkbenchContext context) {
		return new LogOutAction(context);
	}


	// ********** AbstractApplicationNode overrides **********

	protected String[] displayStringPropertyNames() {
		return DATABASE_DISPLAY_STRING_PROPERTY_NAMES;
	}

	protected String[] iconPropertyNames() {
		return DATABASE_ICON_PROPERTY_NAMES;
	}


	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return DatabasePropertiesPage.class;
	}


	// ************ convenience methods ************
	
	public MWDatabase getDatabase() {
		return (MWDatabase) this.getValue();
	}
	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		WorkbenchContext localContext = this.buildLocalWorkbenchContext(workbenchContext);

		RootMenuDescription desc = new RootMenuDescription();
		MenuGroupDescription loginGroup = new MenuGroupDescription();
		loginGroup.add(this.getDatabaseLogInAction(localContext));
		loginGroup.add(this.getDatabaseLogOutAction(localContext));
		desc.add(loginGroup);
		
		MenuGroupDescription addRefreshGroup = new MenuGroupDescription();
		addRefreshGroup.add(this.getAddNewTableAction(localContext));
		addRefreshGroup.add(this.getAddOrRefreshTablesAction(localContext));
		desc.add(addRefreshGroup);
		
		desc.add(this.buildOracleHelpMenuGroup(localContext));
		
		return desc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		WorkbenchContext localContext = this.buildLocalWorkbenchContext(workbenchContext);

		ToolBarDescription desc = new ToolBarDescription();
		ToolBarButtonGroupDescription loginGroup = new ToolBarButtonGroupDescription();
		loginGroup.add(this.getDatabaseLogInAction(localContext));
		loginGroup.add(this.getDatabaseLogOutAction(localContext));
		desc.add(loginGroup);
		
		ToolBarButtonGroupDescription addRefreshGroup = new ToolBarButtonGroupDescription();
		addRefreshGroup.add(this.getAddNewTableAction(localContext));
		addRefreshGroup.add(this.getAddOrRefreshTablesAction(localContext));
		desc.add(addRefreshGroup);
		
		return desc;
	}
    
    public TableNode tableNodeFor(MWTable table) {
        return (TableNode) this.descendantNodeForValue(table);
    }

}

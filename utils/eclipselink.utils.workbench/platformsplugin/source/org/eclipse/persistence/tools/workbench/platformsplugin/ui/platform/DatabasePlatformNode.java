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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsApplicationNode;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPlugin;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.type.DatabaseTypeNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;


/**
 * node for a database platform; the children are the database types
 */
public final class DatabasePlatformNode
	extends PlatformsApplicationNode
{
	private ListValueModel childrenModel;

	protected static final String[] DATABASE_PLATFORM_DISPLAY_STRING_PROPERTY_NAMES = {DatabasePlatform.NAME_PROPERTY};


	// ********** constructor/initialization **********

	public DatabasePlatformNode(DatabasePlatform platform, DatabasePlatformRepositoryNode parent, PlatformsPlugin plugin, ApplicationContext context) {
		super(platform, parent, plugin, context);
	}

	protected void initialize() {
		super.initialize();
		this.childrenModel = this.buildChildrenModel();
	}

	private ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter());
	}

	// the display string of each database type node can change
	private ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildDatabaseTypeNodeAdapter(), DISPLAY_STRING_PROPERTY);
	}

	// transform the platform's collection of database types into nodes
	private ListValueModel buildDatabaseTypeNodeAdapter() {
		return new TransformationListValueModelAdapter(this.buildDatabaseTypesAdapter()) {
			protected Object transformItem(Object item) {
				return DatabasePlatformNode.this.buildDatabaseTypeNode((DatabaseType) item);
			}
		};
	}

	DatabaseTypeNode buildDatabaseTypeNode(DatabaseType databaseType) {
		return new DatabaseTypeNode(databaseType, this, this.getPlatformsPlugin(), this.getApplicationContext());
	}

	// convert the platform's collection of database types to a CollectionValueModel
	private CollectionValueModel buildDatabaseTypesAdapter() {
		return new CollectionAspectAdapter(this, DatabasePlatform.DATABASE_TYPES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((DatabasePlatform) this.subject).databaseTypes();
			}
			protected int sizeFromSubject() {
				return ((DatabasePlatform) this.subject).databaseTypesSize();
			}
		};
	}


	// ********** PlatformsApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return DatabasePlatformTabbedPropertiesPage.class;
	}

	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {
		return new DatabasePlatformTabbedPropertiesPage(context);
	}

	public String helpTopicID() {
		return "database.platform";
	}


	// ********** AbstractApplicationNode overrides **********

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	protected String[] displayStringPropertyNames() {
		return DATABASE_PLATFORM_DISPLAY_STRING_PROPERTY_NAMES;
	}

	protected String buildIconKey() {
		return "DATABASE_PLATFORM";
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);

		RootMenuDescription menu = new RootMenuDescription();

		MenuGroupDescription basicGroup = new MenuGroupDescription();
			basicGroup.add(this.buildRenameAction(context));
			basicGroup.add(this.buildDeleteAction(context));
			basicGroup.add(this.buildCloneAction(context));
			basicGroup.add(this.buildAddTypeAction(context));
		menu.add(basicGroup);
	
		MenuGroupDescription helpGroup = new MenuGroupDescription();
			helpGroup.add(this.getPlatformsPlugin().getHelpAction(context));
		menu.add(helpGroup);
		
		return menu;
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);

		ToolBarDescription toolBar = new ToolBarDescription();

		ToolBarButtonGroupDescription basicGroup = new ToolBarButtonGroupDescription();
			basicGroup.add(this.buildRenameAction(context));
			basicGroup.add(this.buildDeleteAction(context));
			basicGroup.add(this.buildCloneAction(context));
			basicGroup.add(this.buildAddTypeAction(context));
		toolBar.add(basicGroup);

		return toolBar;
	}
	

	// ********** queries **********

	DatabasePlatform getDatabasePlatform() {
		return (DatabasePlatform) this.getValue();
	}


	// ********** behavior **********

	private FrameworkAction buildRenameAction(WorkbenchContext context) {
		return new RenameDatabasePlatformAction(context);
	}

	private FrameworkAction buildDeleteAction(WorkbenchContext context) {
		return new DeleteDatabasePlatformAction(context);
	}

	private FrameworkAction buildCloneAction(WorkbenchContext context) {
		return new CloneDatabasePlatformAction(context);
	}

	private FrameworkAction buildAddTypeAction(WorkbenchContext context) {
		return new AddDatabaseTypeAction(context);
	}

}

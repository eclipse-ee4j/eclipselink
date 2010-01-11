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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.type;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsApplicationNode;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPlugin;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform.DatabasePlatformNode;


/**
 * node for a database type; leaf node (no children)
 */
public final class DatabaseTypeNode
	extends PlatformsApplicationNode
{

	private static final String[] DATABASE_TYPE_DISPLAY_STRING_PROPERTY_NAMES = {DatabaseType.NAME_PROPERTY};


	// ********** constructor **********

	public DatabaseTypeNode(DatabaseType databaseType, DatabasePlatformNode parent, PlatformsPlugin plugin, ApplicationContext context) {
		super(databaseType, parent, plugin, context);
	}


	// ********** PlatformsApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return DatabaseTypePropertiesPage.class;
	}

	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {
		return new DatabaseTypePropertiesPage(context);
	}

	public String helpTopicID() {
		return "database.type";
	}


	// ********** AbstractApplicationNode overrides **********

	protected String[] displayStringPropertyNames() {
		return DATABASE_TYPE_DISPLAY_STRING_PROPERTY_NAMES;
	}

	protected String buildIconKey() {
		return "DATABASE_TYPE";
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);

		RootMenuDescription menu = new RootMenuDescription();

		MenuGroupDescription basicGroup = new MenuGroupDescription();
			basicGroup.add(this.buildRenameAction(context));
			basicGroup.add(this.buildDeleteAction(context));
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
		toolBar.add(basicGroup);

		return toolBar;
	}


	// ********** queries **********

	DatabaseType getDatabaseType() {
		return (DatabaseType) this.getValue();
	}


	// ********** behavior **********

	private FrameworkAction buildRenameAction(WorkbenchContext context) {
		return new RenameDatabaseTypeAction(context);
	}

	private FrameworkAction buildDeleteAction(WorkbenchContext context) {
		return new DeleteDatabaseTypeAction(context);
	}

}

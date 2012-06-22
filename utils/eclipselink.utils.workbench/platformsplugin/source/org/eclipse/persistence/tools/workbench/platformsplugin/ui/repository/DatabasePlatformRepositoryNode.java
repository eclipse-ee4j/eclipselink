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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository;

import java.io.File;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsApplicationNode;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformsPlugin;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform.DatabasePlatformNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;


/**
 * node for a database platform repository; the children are the database platforms
 */
public final class DatabasePlatformRepositoryNode
	extends PlatformsApplicationNode
{

	private ListValueModel childrenModel;

	protected static final String[] DATABASE_PLATFORM_REPOSITORY_DISPLAY_STRING_PROPERTY_NAMES = {DatabasePlatformRepository.NAME_PROPERTY};


	// ********** constructors/initialization **********

	public DatabasePlatformRepositoryNode(DatabasePlatformRepository value, ApplicationContext context, PlatformsPlugin plugin) {
		super(value, context.getNodeManager().getRootNode(), plugin, context);
	}

	protected void initialize() {
		super.initialize();
		this.childrenModel = this.buildChildrenModel();
	}

	// the list should be sorted
	private ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter());
	}

	// the display string of each platform node can change
	private ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildPlatformNodeAdapter(), DISPLAY_STRING_PROPERTY);
	}

	// transform the repository's collection of platforms into nodes
	private ListValueModel buildPlatformNodeAdapter() {
		return new TransformationListValueModelAdapter(this.buildPlatformsAdapter()) {
			protected Object transformItem(Object item) {
				return DatabasePlatformRepositoryNode.this.buildDatabasePlatformNode((DatabasePlatform) item);
			}
		};
	}

	DatabasePlatformNode buildDatabasePlatformNode(DatabasePlatform databasePlatform) {
		return new DatabasePlatformNode(databasePlatform, this, this.getPlatformsPlugin(), this.getApplicationContext());
	}

	// convert the repository's collection of platforms to a CollectionValueModel
	private CollectionValueModel buildPlatformsAdapter() {
		return new CollectionAspectAdapter(this, DatabasePlatformRepository.PLATFORMS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((DatabasePlatformRepository) this.subject).platforms();
			}
			protected int sizeFromSubject() {
				return ((DatabasePlatformRepository) this.subject).platformsSize();
			}
		};
	}


	// ********** PlatformsApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return DatabasePlatformRepositoryTabbedPropertiesPage.class;
	}

	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {
		return new DatabasePlatformRepositoryTabbedPropertiesPage(context);
	}

	public String helpTopicID() {
		return "database.platform.repository";
	}


	// ********** AbstractApplicationNode overrides **********

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	protected String[] displayStringPropertyNames() {
		return DATABASE_PLATFORM_REPOSITORY_DISPLAY_STRING_PROPERTY_NAMES;
	}

	protected String buildIconKey() {
		return "DATABASE_PLATFORM_REPOSITORY";
	}

	public boolean save(File mostRecentSaveDirectory, WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);
		// the save location will be null on new repositories
		if (this.getDatabasePlatformRepository().getFile() == null) {
			File file = this.promptForSaveFile(mostRecentSaveDirectory, context);
			if (file == null) {
				return false;		// user cancelled save
			}
			this.getDatabasePlatformRepository().setFile(file);
		}

		this.getDatabasePlatformRepository().write();
		return true;
	}

	public boolean saveAs(File mostRecentSaveDirectory, WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);
		// the save location will be null on new repositories
		File file = this.promptForSaveFile(mostRecentSaveDirectory, context);
		if (file == null) {
			return false;		// user cancelled save
		}
		this.getDatabasePlatformRepository().setFile(file);
		this.getDatabasePlatformRepository().write();
		return true;
	}

	public File saveFile() {
		return this.getDatabasePlatformRepository().getFile();
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);

		RootMenuDescription menu = new RootMenuDescription();

		MenuGroupDescription basicGroup = new MenuGroupDescription();
			basicGroup.add(buildRenameAction(context));
			basicGroup.add(buildAddPlatformAction(context));
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
			basicGroup.add(buildRenameAction(context));
			basicGroup.add(buildAddPlatformAction(context));
		toolBar.add(basicGroup);
		
		return toolBar;
	}


	// ********** queries **********

	DatabasePlatformRepository getDatabasePlatformRepository() {
		return (DatabasePlatformRepository) this.getValue();
	}


	// ********** behavior **********

	private FrameworkAction buildRenameAction(WorkbenchContext workbenchContext) {
		return new RenameDatabasePlatformRepositoryAction(workbenchContext);
	}

	private FrameworkAction buildAddPlatformAction(WorkbenchContext workbenchContext) {
		return new AddDatabasePlatformAction(workbenchContext);
	}

	private File promptForSaveFile(File mostRecentSaveDirectory, WorkbenchContext context) {
		JFileChooser fileChooser = new JFileChooser(mostRecentSaveDirectory);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle(context.getApplicationContext().getResourceRepository().getString("REPOSITORY_SAVE_FILE_TITLE", this.getDatabasePlatformRepository().getName()));

		while (true) {		// exit loop via a return
			int buttonChoice = fileChooser.showSaveDialog(context.getCurrentWindow());
			if (buttonChoice != JFileChooser.APPROVE_OPTION) {
				return null;
			}
			File file = fileChooser.getSelectedFile();
			if (this.getPlatformsPlugin().fileIsSupported(file)) {
				return file;
			}
			JOptionPane.showMessageDialog(context.getCurrentWindow(), context.getApplicationContext().getResourceRepository().getString("UNSUPPORTED_FILE_TYPE"));
		}
	}

}
